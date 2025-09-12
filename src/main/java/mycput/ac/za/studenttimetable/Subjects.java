package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.Timer;

public class Subjects extends JPanel {

    public interface ConnectionProvider {
        Connection get() throws SQLException;
    }

    private final ConnectionProvider connectionProvider;
    private DefaultTableModel model;
    private JTable table;
    private JTextField search;

    private String studentId;
    private String studentGroup;

    private final List<String> dynamicAssessmentLabels = new ArrayList<>();
    private final Map<String, Integer> labelToColumnIndex = new LinkedHashMap<>();
    private final List<String> rowSubjectCodes = new ArrayList<>();
    private final Map<String, Map<String, Double>> subjectLabelWeights = new HashMap<>();
    private final Map<String, Map<String, Integer>> subjectLabelTermIds = new HashMap<>();

    private final DecimalFormat gradeFmt = new DecimalFormat("0.0");

    public Subjects(ConnectionProvider connectionProvider, String studentId, String studentGroup) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "connectionProvider required");
        setLayout(new BorderLayout());
        setOpaque(false);
        initUI();
        setStudent(studentId, studentGroup);
    }

    private void initUI() {
        add(buildHeader(), BorderLayout.NORTH);

        RoundedCard card = new RoundedCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel hTitle = new JLabel("Subject Grade Calculator");
        hTitle.setFont(getPoppins(22f, Font.BOLD));
        hTitle.setForeground(new Color(33, 37, 41));

        JLabel hSub = new JLabel("Edit term marks, then calculate final grades — instantly.");
        hSub.setFont(getPoppins(12f, Font.PLAIN));
        hSub.setForeground(new Color(96, 100, 105));

        JPanel titleBlock = new JPanel(new BorderLayout());
        titleBlock.setOpaque(false);
        titleBlock.add(hTitle, BorderLayout.NORTH);
        titleBlock.add(hSub, BorderLayout.SOUTH);
        card.add(titleBlock, BorderLayout.NORTH);

        model = createModel(new String[]{"Subject", "Final Grade"});
        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(getPoppins(14f, Font.PLAIN));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setDefaultRenderer(new HeaderRenderer());
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 44));

        table.setDefaultRenderer(Number.class, new DefaultTableCellRenderer() {{
            setHorizontalAlignment(SwingConstants.CENTER);
        }});

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                else c.setBackground(new Color(220, 235, 251));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                int finalCol = table.getColumnCount() - 1;
                if (column == finalCol && value != null) setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        table.setDefaultEditor(Number.class, new NumericEditor());
        table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(new FinalGradeRenderer());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        sp.getVerticalScrollBar().setUnitIncrement(12);
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = new Color(200, 200, 200); }
        });
        card.add(sp, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        controls.setOpaque(false);
        JButton calc = new JButton("Calculate"); calc.addActionListener(e -> calculateAll());
        JButton export = new JButton("Export CSV"); export.addActionListener(e -> exportCSV());
        JButton copy = new JButton("Copy Grades"); copy.addActionListener(e -> copyToClipboard());
        controls.add(copy); controls.add(export); controls.add(calc);
        card.add(controls, BorderLayout.SOUTH);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(card);
        add(centerWrap, BorderLayout.CENTER);

        card.pulse();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 18, 6, 18));

        JLabel appTitle = new JLabel("📘 My Subjects");
        appTitle.setFont(getPoppins(18f, Font.BOLD));
        appTitle.setForeground(new Color(33, 37, 41));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        search = new JTextField(18);
        search.setPreferredSize(new Dimension(220, 34));
        search.setFont(getPoppins(13f, Font.PLAIN));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(6, 8, 6, 8)));
        search.setToolTipText("Filter subjects by name...");
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                if (model == null) return;
                String txt = search.getText().trim();
                TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
                if (txt.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(txt), 0));
                table.setRowSorter(sorter);
            }
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        right.add(search);
        p.add(appTitle, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    public void setStudent(String studentId, String studentGroup) {
    this.studentId = studentId;
    this.studentGroup = studentGroup;

    System.out.println("Subjects.setStudent() called: " + studentId + ", " + studentGroup);

    // Reload data from DB immediately
    reloadFromDB();
}


    private void reloadFromDB() {
        if (studentGroup == null || studentGroup.isEmpty()) {
            clearTableToEmpty(); return;
        }

        List<DbSubject> subjects = fetchSubjects();
        if (subjects.isEmpty()) { subjects.add(new DbSubject("SUB101","Sample Subject 1")); subjects.add(new DbSubject("SUB102","Sample Subject 2")); }

        dynamicAssessmentLabels.clear(); labelToColumnIndex.clear(); subjectLabelWeights.clear(); subjectLabelTermIds.clear();
        int colIndex = 1;
        Map<String, Map<Integer, Double>> marks = new HashMap<>();

        for (DbSubject s : subjects) {
            List<DbTerm> terms = fetchTerms(s.code);
            if (terms.isEmpty()) { terms.add(new DbTerm(1,"Term 1",1.0)); terms.add(new DbTerm(2,"Term 2",1.0)); }

            Map<String, Double> weights = new HashMap<>();
            Map<String, Integer> lblToTermId = new HashMap<>();

            for (DbTerm t : terms) {
                if (!dynamicAssessmentLabels.contains(t.label)) { dynamicAssessmentLabels.add(t.label); labelToColumnIndex.put(t.label, colIndex++); }
                weights.put(t.label,t.weight); lblToTermId.put(t.label,t.id);
            }
            subjectLabelWeights.put(s.code, weights); subjectLabelTermIds.put(s.code,lblToTermId);

            Map<Integer, Double> termMarks = new HashMap<>();
            for (DbTerm t : terms) termMarks.put(t.id, Math.random()*100);
            marks.put(s.code, termMarks);
        }

        buildTable(subjects, marks);
    }

    public List<DbSubject> fetchSubjects() {
        List<DbSubject> subjects = new ArrayList<>();
        String courseId = null;
        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement("SELECT CourseID FROM StudentGroup WHERE GroupID=?")) {
            ps.setString(1, studentGroup);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) courseId = rs.getString("CourseID"); }
        } catch (SQLException ignored) {}
        if (courseId == null) return subjects;

        int yearLevel = 1; try { yearLevel = Integer.parseInt(studentGroup.substring(0,1)); } catch (Exception ignored) {}
        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT s.SubjectCode, s.SubjectName FROM Subject s " +
                             "JOIN SubjectCourse sc ON s.SubjectCode = sc.SubjectCode " +
                             "WHERE sc.CourseID=? AND s.YearLevel=? ORDER BY s.SubjectName")) {
            ps.setString(1, courseId); ps.setInt(2, yearLevel);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) subjects.add(new DbSubject(rs.getString("SubjectCode"), rs.getString("SubjectName"))); }
        } catch (SQLException ignored) {}
        return subjects;
    }

    private List<DbTerm> fetchTerms(String subjectCode) {
        List<DbTerm> terms = new ArrayList<>();
        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement("SELECT TermID, TermName, Weight FROM TermDefinition WHERE SubjectCode=? ORDER BY TermID")) {
            ps.setString(1, subjectCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) terms.add(new DbTerm(rs.getInt("TermID"), safe(rs.getString("TermName")), getNullableDouble(rs,"Weight")));
            }
        } catch (SQLException ignored) {}
        return terms;
    }

    private void buildTable(List<DbSubject> subjects, Map<String, Map<Integer, Double>> marks) {
        List<String> cols = new ArrayList<>();
        cols.add("Subject"); cols.addAll(dynamicAssessmentLabels); cols.add("Final Grade");
        model.setDataVector(new Object[0][0], cols.toArray()); rowSubjectCodes.clear();

        for (DbSubject s : subjects) {
            rowSubjectCodes.add(s.code);
            Object[] row = new Object[cols.size()];
            row[0] = s.name;
            Map<Integer, Double> m = marks.getOrDefault(s.code, Collections.emptyMap());
            for (String lbl : dynamicAssessmentLabels) {
                Integer col = labelToColumnIndex.get(lbl);
                Integer tid = subjectLabelTermIds.getOrDefault(s.code, Collections.emptyMap()).get(lbl);
                row[col] = (tid != null) ? m.getOrDefault(tid,null) : null;
            }
            model.addRow(row);
            int newRow = model.getRowCount()-1;
            model.setValueAt(computeFinalForRow(newRow,s.code), newRow, row.length-1);
        }
    }

    private double computeFinalForRow(int rowIndex, String subjectCode) {
        Map<String, Double> weights = subjectLabelWeights.getOrDefault(subjectCode, Collections.emptyMap());
        double weightedSum = 0.0, weightTotal = 0.0, sum=0.0; int count=0;
        for (String lbl: dynamicAssessmentLabels) {
            Integer col = labelToColumnIndex.get(lbl);
            if (col == null) continue;
            Object val = model.getValueAt(rowIndex, col);
            if (val == null) continue;
            double mark = toDouble(val);
            Double w = weights.get(lbl);
            if (w!=null && w>0) { weightedSum += mark*w; weightTotal += w; }
            sum += mark; count++;
        }
        if(weightTotal>0) return weightedSum/weightTotal;
        if(count>0) return sum/count;
        return 0.0;
    }

    private void clearTableToEmpty() { if(model!=null) { model.setRowCount(0); model.setColumnCount(0); } }
    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns,0){
            @Override public boolean isCellEditable(int row,int col){ int last=getColumnCount()-1; return col>0 && col<last; }
            @Override public Class<?> getColumnClass(int col){ return col==0?String.class:Double.class; }
        };
    }

    private double toDouble(Object val){ if(val instanceof Number) return ((Number)val).doubleValue(); if(val!=null) try{ return Double.parseDouble(val.toString()); }catch(Exception ignored){} return 0.0; }

    private void calculateAll(){ for(int i=0;i<model.getRowCount();i++) model.setValueAt(computeFinalForRow(i,rowSubjectCodes.get(i)),i,model.getColumnCount()-1); }
    private void exportCSV(){ System.out.println("Export CSV - implement your logic"); }
    private void copyToClipboard(){ System.out.println("Copy grades - implement your logic"); }
    private static String safe(String s){ return s==null?"":s.trim(); }
    private static Double getNullableDouble(ResultSet rs,String col) throws SQLException{ BigDecimal bd=rs.getBigDecimal(col); return bd!=null?bd.doubleValue():0.0; }
    private Font getPoppins(float size,int style){ Font f=new Font("Poppins",style,(int)size); if(!f.getFamily().equals("Poppins")) f=new Font("SansSerif",style,(int)size); return f.deriveFont(size); }

    public static class DbSubject { final String code,name; DbSubject(String c,String n){code=c;name=n;} }
    public static class DbTerm { final int id; final String label; final double weight; DbTerm(int id,String label,double w){this.id=id;this.label=label;this.weight=w;} }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer(){ setHorizontalAlignment(SwingConstants.CENTER); setOpaque(true); setBorder(new EmptyBorder(10,8,10,8)); }
        @Override public Component getTableCellRendererComponent(JTable table,Object value,boolean sel,boolean focus,int row,int col){
            super.getTableCellRendererComponent(table,value,sel,focus,row,col);
            setBackground(new Color(33,150,243)); setForeground(Color.WHITE); setFont(new Font("SansSerif",Font.BOLD,13)); return this;
        }
    }

    private class FinalGradeRenderer extends DefaultTableCellRenderer {
        FinalGradeRenderer(){ setHorizontalAlignment(SwingConstants.CENTER);}
        @Override public Component getTableCellRendererComponent(JTable table,Object value,boolean sel,boolean focus,int row,int col){
            Component c = super.getTableCellRendererComponent(table,value,sel,focus,row,col);
            if(value instanceof Number){
                double g = ((Number)value).doubleValue();
                setText(gradeFmt.format(g));
                if(g>=75)c.setForeground(new Color(27,150,84));
                else if(g>=50)c.setForeground(new Color(245,140,31));
                else c.setForeground(new Color(220,53,69));
            } else setText("-");
            return c;
        }
    }

    private static class NumericEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner;
        NumericEditor(){
            spinner = new JSpinner(new SpinnerNumberModel(0.0,0.0,100.0,1.0));
            JComponent editor = spinner.getEditor();
            if(editor instanceof JSpinner.DefaultEditor)((JSpinner.DefaultEditor)editor).getTextField().setHorizontalAlignment(SwingConstants.CENTER);
            spinner.setBorder(new EmptyBorder(0,4,0,4));
        }
        @Override public Object getCellEditorValue(){ return spinner.getValue(); }
        @Override public Component getTableCellEditorComponent(JTable table,Object value,boolean sel,int row,int col){
            if(value instanceof Number) spinner.setValue(((Number)value).doubleValue());
            else if(value!=null) try { spinner.setValue(Double.parseDouble(value.toString())); } catch(Exception ignored){ spinner.setValue(0.0); }
            return spinner;
        }
    }

    private class RoundedCard extends JPanel {
        private Color base=Color.WHITE; private int pulse=0;
        RoundedCard(){ setOpaque(false); setPreferredSize(new Dimension(920,420)); }
        void pulse(){ Timer t = new Timer(18,null); t.addActionListener(new AbstractAction(){ int step=0; @Override public void actionPerformed(ActionEvent e){ step++; pulse=(int)(6*Math.sin(step*Math.PI/40.0)); repaint(); if(step>80){((Timer)e.getSource()).stop(); pulse=0; repaint();}}}); t.start();}
        @Override protected void paintComponent(Graphics g){ int w=getWidth(),h=getHeight(); Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); for(int i=0;i<8;i++){ int alpha=20-i*2; g2.setColor(new Color(0,0,0,Math.max(0,alpha))); int x=8-i+pulse/2,y=8-i+Math.abs(pulse/3); g2.fillRoundRect(x,y,w-(8-i)*2,h-(8-i)*2,18,18);} g2.setColor(base); g2.fillRoundRect(0,0,w-12,h-12,18,18); g2.dispose(); super.paintComponent(g);}
    }
}
