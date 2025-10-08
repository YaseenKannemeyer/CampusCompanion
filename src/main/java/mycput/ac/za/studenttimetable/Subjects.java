package mycput.ac.za.studenttimetable;
//working
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
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
    private HeaderBannerPanel headerBanner;

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

    // ---------------- UI ------------------------
private void initUI() {
    // Header panel
    headerBanner = new HeaderBannerPanel(connectionProvider, studentId);
    add(headerBanner, BorderLayout.NORTH);

    // Card container with gradient
    ModernCard card = new ModernCard(new Color(41, 128, 185), new Color(72, 196, 230));
    card.setLayout(new BorderLayout());
    card.setBorder(new EmptyBorder(40, 40, 40, 40));

    // Title Block
    JLabel hTitle = new JLabel("Subject Grade Calculator");
    hTitle.setFont(getPoppins(28f, Font.BOLD));
    hTitle.setForeground(Color.WHITE);

    JLabel hSub = new JLabel("Edit term marks and calculate final grades instantly.");
    hSub.setFont(getPoppins(15f, Font.PLAIN));
    hSub.setForeground(new Color(200, 220, 240));

    JPanel titleBlock = new JPanel();
    titleBlock.setOpaque(false);
    titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
    titleBlock.add(hTitle);
    titleBlock.add(Box.createVerticalStrut(6));
    titleBlock.add(hSub);
    card.add(titleBlock, BorderLayout.NORTH);

    // Table setup
    model = createModel(new String[]{"Subject", "Code", "Final Grade"});
    table = new JTable(model);
    table.setRowHeight(48);
    table.setFont(getPoppins(15f, Font.PLAIN));
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setFillsViewportHeight(true);
    table.setSelectionBackground(new Color(220, 235, 251));
    table.setSelectionForeground(Color.BLACK);

    JTableHeader th = table.getTableHeader();
    th.setDefaultRenderer(new HeaderRenderer());
    th.setReorderingAllowed(false);
    th.setPreferredSize(new Dimension(th.getPreferredSize().width, 48));

    table.setDefaultRenderer(Number.class, new DefaultTableCellRenderer() {{ setHorizontalAlignment(CENTER); }});
    table.setDefaultRenderer(Object.class, new TableCellRenderer());
    table.setDefaultEditor(Number.class, new NumericEditor());
    table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(new FinalGradeRenderer());

    JScrollPane sp = new JScrollPane(table);
    sp.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    sp.setPreferredSize(new Dimension(1000, 600));
    sp.getVerticalScrollBar().setUI(new ModernScrollBarUI());
    card.add(sp, BorderLayout.CENTER);

    // Buttons
    JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
    controls.setOpaque(false);
    controls.add(createModernButton("Copy Grades", new Color(33,150,243), e -> copyToClipboard()));
    controls.add(createModernButton("Export CSV", new Color(76,175,80), e -> exportCSV()));
    controls.add(createModernButton("Export SVG", new Color(156,39,176), e -> exportSVG()));
    controls.add(createModernButton("Calculate", new Color(255,193,7), e -> calculateAll()));
    card.add(controls, BorderLayout.SOUTH);

    // Center card
    JPanel centerWrap = new JPanel(new GridBagLayout());
    centerWrap.setOpaque(false);
    centerWrap.add(card, new GridBagConstraints() {{
        fill = GridBagConstraints.BOTH;
        weightx = 1.0;
        weighty = 1.0;
    }});
    add(centerWrap, BorderLayout.CENTER);

    card.pulse();
}



//    private JPanel buildHeader() {
//        JPanel p = new JPanel(new BorderLayout());
//        p.setOpaque(false);
//        p.setBorder(new EmptyBorder(18, 18, 6, 18));
//
//        JLabel appTitle = new JLabel("📘 My Subjects");
//        appTitle.setFont(getPoppins(18f, Font.BOLD));
//        appTitle.setForeground(new Color(33, 37, 41));
//
//        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
//        right.setOpaque(false);
//
//        search = new JTextField(18);
//        search.setPreferredSize(new Dimension(240, 34));
//        search.setFont(getPoppins(13f, Font.PLAIN));
//        search.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(180,180,180),1,true),
//                new EmptyBorder(6, 12, 6, 12)));
//        search.setToolTipText("Filter subjects...");
//        search.getDocument().addDocumentListener(new DocumentListener() {
//            private void filter() {
//                if (model == null) return;
//                String txt = search.getText().trim();
//                TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
//                if (txt.isEmpty()) sorter.setRowFilter(null);
//                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(txt), 0));
//                table.setRowSorter(sorter);
//            }
//            public void insertUpdate(DocumentEvent e) { filter(); }
//            public void removeUpdate(DocumentEvent e) { filter(); }
//            public void changedUpdate(DocumentEvent e) { filter(); }
//        });
//
//// Create a search emoji label
//JLabel searchIcon = new JLabel("🔍");
//searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 16)); // adjust size
//searchIcon.setBorder(new EmptyBorder(0, 5, 0, 5)); // spacing
//
//// Wrap emoji and search field in a panel
//JPanel searchPanel = new JPanel(new BorderLayout());
//searchPanel.setOpaque(false);
//searchPanel.add(searchIcon, BorderLayout.WEST);
//searchPanel.add(search, BorderLayout.CENTER);
//
//// Add the wrapped panel to the header
//right.add(searchPanel);
//        p.add(appTitle, BorderLayout.WEST);
//        p.add(right, BorderLayout.EAST);
//        return p;
//    }

    private JButton createModernButton(String text, Color color, java.awt.event.ActionListener listener){
    JButton btn = new JButton(text);
    btn.setFont(getPoppins(13f, Font.BOLD));
    btn.setBackground(color);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setBorder(new EmptyBorder(10, 20, 10, 20));
    btn.addActionListener(listener);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Smooth hover
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e){ btn.setBackground(color.brighter()); }
        @Override
        public void mouseExited(java.awt.event.MouseEvent e){ btn.setBackground(color); }
    });
    return btn;
}
    private class RoundedBorder implements javax.swing.border.Border {
        private final int radius; RoundedBorder(int r){radius=r;}
        @Override public Insets getBorderInsets(Component c){ return new Insets(radius,radius,radius,radius);}
        @Override public boolean isBorderOpaque(){ return true; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
            g.setColor(c.getForeground());
            g.drawRoundRect(x,y,w-1,h-1,radius,radius);
        }
    }

    // Header refinements
private static class HeaderRenderer extends DefaultTableCellRenderer {
    HeaderRenderer(){
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        setBorder(new EmptyBorder(10,8,10,8));
        setFont(new Font("Poppins",Font.BOLD,14));
        setForeground(Color.WHITE);
        setBackground(new Color(41,128,185));
    }
}

private class TableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value,
                                                   boolean isSelected,boolean hasFocus,
                                                   int row,int column){
        Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        if(!isSelected) c.setBackground(row%2==0 ? new Color(72,196,230,60) : new Color(41,128,185,40));
        setBorder(new EmptyBorder(6,12,6,12));
        setHorizontalAlignment(column==0 ? LEFT : CENTER);
        setForeground(Color.BLACK);
        return c;
    }
}

    // ---------------- Table + Scrollbar Styling ----------------
private static class ModernScrollBarUI extends BasicScrollBarUI {
    @Override protected void configureScrollBarColors(){
        thumbColor = new Color(185,186,163);
        trackColor = new Color(214,213,201);
    }
}

    private Font getPoppins(float size,int style){
        Font f = new Font("Poppins",style,(int)size);
        if(!f.getFamily().equals("Poppins")) f = new Font("SansSerif",style,(int)size);
        return f.deriveFont(size);
    }

    // ---------------- Functional ----------------
    public void setStudent(String studentId, String studentGroup) {
    this.studentId = studentId;
    this.studentGroup = studentGroup;

    // Update header panel
    if (headerBanner != null) {
        headerBanner.setStudentId(studentId);  // we'll add this method in HeaderBannerPanel
    }

    reloadFromDB();
}


    private void reloadFromDB() {
    if (studentGroup == null || studentGroup.isEmpty()) {
        model.setRowCount(0);
        model.setColumnCount(0);
        return;
    }

    List<DbSubject> subjects = fetchSubjects();
    if (subjects.isEmpty()) {
        // fallback demo subjects
        subjects.add(new DbSubject("SUB101", "Sample Subject 1"));
        subjects.add(new DbSubject("SUB102", "Sample Subject 2"));
    }

    // clear previous data
    dynamicAssessmentLabels.clear();
    labelToColumnIndex.clear();
    subjectLabelWeights.clear();
    subjectLabelTermIds.clear();
    rowSubjectCodes.clear();

    int colIndex = 2; // start AFTER Subject (0) + Subject Code (1)
    Map<String, Map<Integer, Double>> marks = new HashMap<>();

    for (DbSubject s : subjects) {
        List<DbTerm> terms = fetchTerms(s.code);
        if (terms.isEmpty()) {
            // fallback demo terms
            terms.add(new DbTerm(1, "Term 1", 1.0));
            terms.add(new DbTerm(2, "Term 2", 1.0));
            terms.add(new DbTerm(3, "Term 3", 1.0));
            terms.add(new DbTerm(4, "Term 4", 1.0));
        }

        Map<String, Double> weights = new HashMap<>();
        Map<String, Integer> lblToTermId = new HashMap<>();

        for (DbTerm t : terms) {
            if (!dynamicAssessmentLabels.contains(t.label)) {
                dynamicAssessmentLabels.add(t.label);
                labelToColumnIndex.put(t.label, colIndex++);
            }
            weights.put(t.label, t.weight);
            lblToTermId.put(t.label, t.id);
        }

        subjectLabelWeights.put(s.code, weights);
        subjectLabelTermIds.put(s.code, lblToTermId);

        // generate demo marks
        Map<Integer, Double> termMarks = new HashMap<>();
        for (DbTerm t : terms) termMarks.put(t.id, Math.random() * 100);
        marks.put(s.code, termMarks);
    }

    // build table
    buildTable(subjects, marks);
    setColumnWidths();
}


    private List<DbSubject> fetchSubjects() {
        List<DbSubject> subjects = new ArrayList<>();
        if (studentGroup == null) return subjects;
        String courseId = null;
        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement("SELECT CourseID FROM StudentGroup WHERE GroupID=?")) {
            ps.setString(1, studentGroup);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) courseId = rs.getString("CourseID"); }
        } catch (SQLException ignored) {}
        if (courseId == null) return subjects;

        int yearLevel = 1; 
        try { yearLevel = Integer.parseInt(studentGroup.substring(0,1)); } catch (Exception ignored) {}
        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT s.SubjectCode, s.SubjectName FROM Subject s " +
                             "JOIN SubjectCourse sc ON s.SubjectCode = sc.SubjectCode " +
                             "WHERE sc.CourseID=? AND s.YearLevel=? ORDER BY s.SubjectName")) {
            ps.setString(1, courseId); ps.setInt(2, yearLevel);
            try (ResultSet rs = ps.executeQuery()) { 
                while (rs.next()) subjects.add(new DbSubject(rs.getString("SubjectCode"), rs.getString("SubjectName"))); 
            }
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
    // Columns: Subject | Subject Code | Dynamic Terms... | Final Grade
    List<String> cols = new ArrayList<>();
    cols.add("Subject");
    cols.add("Subject Code");
    cols.addAll(dynamicAssessmentLabels);
    cols.add("Final Grade");

    model.setDataVector(new Object[0][0], cols.toArray());
    rowSubjectCodes.clear();

    for (DbSubject s : subjects) {
        rowSubjectCodes.add(s.code);
        Object[] row = new Object[cols.size()];

        // Subject info
        row[0] = s.name;
        row[1] = s.code;

        // dynamic term marks
        Map<Integer, Double> m = marks.getOrDefault(s.code, Collections.emptyMap());
        for (String lbl : dynamicAssessmentLabels) {
            Integer col = labelToColumnIndex.get(lbl);
            if (col == null) continue;
            Integer tid = subjectLabelTermIds.getOrDefault(s.code, Collections.emptyMap()).get(lbl);
            row[col] = (tid != null) ? m.getOrDefault(tid, null) : null;
        }

        model.addRow(row);

        // compute final grade
        int newRow = model.getRowCount() - 1;
        model.setValueAt(computeFinalForRow(newRow, s.code), newRow, row.length - 1);
    }
}


    
    private void setColumnWidths() {
    if (table.getColumnModel().getColumnCount() == 0) return;

    TableColumnModel colModel = table.getColumnModel();

    // First column (Subject)
    TableColumn col0 = colModel.getColumn(0);
    col0.setPreferredWidth(470);  // width in pixels
    col0.setMinWidth(200);
    col0.setMaxWidth(470);
    // Second column (Subject Code)
TableColumn col1 = colModel.getColumn(1);
col1.setPreferredWidth(130);  // adjust as needed
col1.setMinWidth(100);
col1.setMaxWidth(130);


    // Other columns (dynamic assessments + Final Grade)
    for (int i = 2; i < colModel.getColumnCount(); i++) {
        TableColumn col = colModel.getColumn(i);
        col.setPreferredWidth(106);
        col.setMinWidth(50);
        col.setMaxWidth(106);
    }

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // important
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
            if(w!=null && w>0){ weightedSum+=mark*w; weightTotal+=w;}
            sum+=mark; count++;
        }
        if(weightTotal>0) return weightedSum/weightTotal;
        if(count>0) return sum/count;
        return 0.0;
    }

    private double toDouble(Object val){ 
        if(val instanceof Number) return ((Number)val).doubleValue(); 
        if(val!=null) try{ return Double.parseDouble(val.toString()); }catch(Exception ignored){} 
        return 0.0; 
    }

    private void calculateAll(){ 
        for(int i=0;i<model.getRowCount();i++) 
            model.setValueAt(computeFinalForRow(i,rowSubjectCodes.get(i)),i,model.getColumnCount()-1); 
    }

    private void copyToClipboard() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < table.getRowCount(); r++)
            for (int c = 0; c < table.getColumnCount(); c++)
                sb.append(table.getValueAt(r, c)).append(c < table.getColumnCount() - 1 ? "," : "\n");
        StringSelection selection = new StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        JOptionPane.showMessageDialog(this, "Grades copied to clipboard!");
    }

    private void exportCSV() {
        try (FileWriter fw = new FileWriter("subjects.csv")) {
            for (int c = 0; c < table.getColumnCount(); c++) fw.append(table.getColumnName(c)).append(c<table.getColumnCount()-1?",":"\n");
            for (int r = 0; r < table.getRowCount(); r++)
                for (int c = 0; c < table.getColumnCount(); c++)
                    fw.append(String.valueOf(table.getValueAt(r,c))).append(c<table.getColumnCount()-1?",":"\n");
            JOptionPane.showMessageDialog(this,"CSV exported successfully!");
        } catch(IOException ex){ ex.printStackTrace(); }
    }

    private void exportSVG() {
        StringBuilder sb = new StringBuilder();
        sb.append("<svg xmlns='http://www.w3.org/2000/svg' width='800' height='").append(30*table.getRowCount()+50).append("'>");
        sb.append("<style>text{font-family:sans-serif;font-size:12px;}</style>");
        int y = 20;
        for(int c=0;c<table.getColumnCount();c++) sb.append("<text x='").append(c*150+10).append("' y='").append(y).append("'>").append(table.getColumnName(c)).append("</text>");
        y+=20;
        for(int r=0;r<table.getRowCount();r++)
            for(int c=0;c<table.getColumnCount();c++)
                sb.append("<text x='").append(c*150+10).append("' y='").append(y).append("'>").append(table.getValueAt(r,c)).append("</text>");
        sb.append("</svg>");
        try(FileWriter fw=new FileWriter("subjects.svg")){ fw.write(sb.toString()); JOptionPane.showMessageDialog(this,"SVG exported successfully!"); }
        catch(IOException ex){ ex.printStackTrace(); }
    }

    private DefaultTableModel createModel(String[] columns) {
    return new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            int last = getColumnCount() - 1;
            return col > 1 && col < last; // now only dynamic assessment columns are editable
        }

        @Override
        public Class<?> getColumnClass(int col) {
            // Subject and Subject Code are Strings, all others are Double
            if (col == 0 || col == 1) return String.class;
            return Double.class;
        }
    };
}


    private static Double getNullableDouble(ResultSet rs,String col) throws SQLException{ BigDecimal bd=rs.getBigDecimal(col); return bd!=null?bd.doubleValue():0.0; }
    private static String safe(String s){ return s==null?"":s.trim(); }

    // ---------------- UI Helpers ----------------
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

    private class NumericEditor extends AbstractCellEditor implements TableCellEditor {
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

    // ---------------- ModernCard with gradient ----------------
private class ModernCard extends JPanel {
    private final Color top, bottom;
    private int pulse = 0;

    ModernCard(Color top, Color bottom) {
        this.top = top;
        this.bottom = bottom;
        setOpaque(false);
        setPreferredSize(new Dimension(920, 600));
    }

    void pulse() {
        Timer t = new Timer(18,null);
        t.addActionListener(new AbstractAction() {
            int step=0;
            @Override
            public void actionPerformed(ActionEvent e){
                step++;
                pulse=(int)(6*Math.sin(step*Math.PI/40.0));
                repaint();
                if(step>80){((Timer)e.getSource()).stop(); pulse=0; repaint();}
            }
        });
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g){
        int w=getWidth(),h=getHeight();
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow layers
        for(int i=0;i<8;i++){
            int alpha=20-i*2;
            g2.setColor(new Color(0,0,0,Math.max(0,alpha)));
            int x=8-i+pulse/2,y=8-i+Math.abs(pulse/3);
            g2.fillRoundRect(x,y,w-(8-i)*2,h-(8-i)*2,18,18);
        }

        // Gradient background
        GradientPaint gp = new GradientPaint(0,0,top,0,h,bottom);
        g2.setPaint(gp);
        g2.fillRoundRect(0,0,w-12,h-12,18,18);

        g2.dispose();
        super.paintComponent(g);
    }
}
    public static class DbSubject { final String code,name; DbSubject(String c,String n){code=c;name=n;} }
    public static class DbTerm { final int id; final String label; final double weight; DbTerm(int id,String label,double w){this.id=id;this.label=label;this.weight=w;} }
}
