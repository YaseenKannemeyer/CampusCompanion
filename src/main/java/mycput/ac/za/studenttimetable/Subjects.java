package mycput.ac.za.studenttimetable;

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

public class Subjects extends JPanel {

    // ---------------- COLORS ----------------
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public interface ConnectionProvider {
        Connection get() throws SQLException;
    }

    private final ConnectionProvider connectionProvider;
    private DefaultTableModel model;
    private JTable table;
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
        setBackground(PRIMARY_BG);
        initUI();
        setStudent(studentId, studentGroup);
    }

    // ---------------- UI ------------------------
private void initUI() {
    // ===== HEADER =====
    headerBanner = new HeaderBannerPanel(connectionProvider, studentId);
    add(headerBanner, BorderLayout.NORTH);

    // ===== CARD CONTAINER =====
    ModernCard card = new ModernCard(Color.decode("#D6EEFF"));
    card.setLayout(new BorderLayout());
    card.setBorder(new EmptyBorder(30, 30, 30, 30));

    // ===== TITLE & SUBTITLE =====
    JLabel hTitle = new JLabel("Subject Grade Calculator");
    hTitle.setFont(getRoboto(24f, Font.BOLD));
    hTitle.setForeground(CTA_SECONDARY);

    JLabel hSub = new JLabel("Edit term marks and calculate final grades instantly.");
    hSub.setFont(getRoboto(14f, Font.PLAIN));
    hSub.setForeground(new Color(80, 80, 80));

    // ===== CREATE MODEL & TABLE FIRST =====
    model = createModel(new String[]{"Subject", "Code", "Final Grade"});
    table = new JTable(model);
    table.setRowHeight(46);
    table.setFont(getRoboto(13f, Font.PLAIN));
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setFillsViewportHeight(true);
    table.setSelectionBackground(new Color(220, 235, 251));
    table.setSelectionForeground(Color.BLACK);

    JTableHeader th = table.getTableHeader();
    th.setDefaultRenderer(new HeaderRenderer());
    th.setReorderingAllowed(false);
    th.setPreferredSize(new Dimension(th.getPreferredSize().width, 40));

    table.setDefaultRenderer(Number.class, new DefaultTableCellRenderer() {{
        setHorizontalAlignment(CENTER);
    }});
    table.setDefaultRenderer(Object.class, new TableCellRenderer());
    table.setDefaultEditor(Number.class, new NumericEditor());
    table.getColumnModel()
         .getColumn(table.getColumnCount() - 1)
         .setCellRenderer(new FinalGradeRenderer());

    JScrollPane sp = new JScrollPane(table);
    sp.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    sp.setPreferredSize(new Dimension(1000, 550));
    sp.getVerticalScrollBar().setUI(new ModernScrollBarUI());

    // ===== SEARCH FIELD =====
    JTextField searchField = new JTextField(20);
    searchField.putClientProperty("JTextField.placeholderText", "Search subjects...");
    searchField.setFont(getRoboto(13f, Font.PLAIN));
    searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
    ));
    searchField.setMaximumSize(new Dimension(250, 36));

    // ===== SORTER + FILTER =====
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    table.setRowSorter(sorter);

    searchField.getDocument().addDocumentListener(new DocumentListener() {
        private void update() {
            String text = searchField.getText().trim();
            if (text.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }
        @Override public void insertUpdate(DocumentEvent e) { update(); }
        @Override public void removeUpdate(DocumentEvent e) { update(); }
        @Override public void changedUpdate(DocumentEvent e) { update(); }
    });

    // ===== TITLE BLOCK =====
    JPanel titleTop = new JPanel(new BorderLayout());
    titleTop.setOpaque(false);
    titleTop.add(hTitle, BorderLayout.WEST);
    titleTop.add(searchField, BorderLayout.EAST);

    JPanel titleBlock = new JPanel();
    titleBlock.setOpaque(false);
    titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
    titleBlock.add(titleTop);
    titleBlock.add(Box.createVerticalStrut(6));
    titleBlock.add(hSub);

    card.add(titleBlock, BorderLayout.NORTH);
    card.add(sp, BorderLayout.CENTER);

    // ===== BUTTONS =====
    JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
    controls.setOpaque(false);
    controls.add(createStyledButton("Copy Grades", e -> copyToClipboard()));
    controls.add(createStyledButton("Export CSV", e -> exportCSV()));
    controls.add(createStyledButton("Export SVG", e -> exportSVG()));
    controls.add(createStyledButton("Calculate", e -> calculateAll()));
    card.add(controls, BorderLayout.SOUTH);

    // ===== CENTER WRAPPER =====
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


    private JButton createStyledButton(String text, java.awt.event.ActionListener listener) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Roboto", Font.BOLD, 13));
    btn.setBackground(CTA_PRIMARY);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setPreferredSize(new Dimension(130, 38));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setBorder(BorderFactory.createEmptyBorder());
    btn.setOpaque(true);

    // Hover effect
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            btn.setBackground(CTA_SECONDARY);
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            btn.setBackground(CTA_PRIMARY);
        }
    });

    btn.addActionListener(listener);
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

    // ---------------- Table + Scrollbar Styling ----------------
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        HeaderRenderer(){
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            setBorder(new EmptyBorder(6,8,6,8));
            setFont(new Font("Roboto",Font.BOLD,13));
            setForeground(Color.WHITE);
            setBackground(CTA_SECONDARY);
        }
    }

    private class TableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,Object value,
                                                       boolean isSelected,boolean hasFocus,
                                                       int row,int column){
            Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            if(!isSelected) c.setBackground(row%2==0 ? new Color(214,238,255,100) : new Color(198,220,240,80));
            setBorder(new EmptyBorder(6,12,6,12));
            setHorizontalAlignment(column==0 ? LEFT : CENTER);
            setForeground(Color.BLACK);
            return c;
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors(){
            thumbColor = new Color(185,186,163);
            trackColor = new Color(214,213,201);
        }
    }

    private Font getRoboto(float size,int style){
        Font f = new Font("Roboto",style,(int)size);
        if(!f.getFamily().equals("Roboto")) f = new Font("SansSerif",style,(int)size);
        return f.deriveFont(size);
    }

    // ---------------- Functional ----------------
    public void setStudent(String studentId, String studentGroup) {
        this.studentId = studentId;
        this.studentGroup = studentGroup;

        if (headerBanner != null) headerBanner.setStudentId(studentId);
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
            subjects.add(new DbSubject("SUB101", "Sample Subject 1"));
            subjects.add(new DbSubject("SUB102", "Sample Subject 2"));
        }

        dynamicAssessmentLabels.clear();
        labelToColumnIndex.clear();
        subjectLabelWeights.clear();
        subjectLabelTermIds.clear();
        rowSubjectCodes.clear();

        int colIndex = 2; // after Subject and Code
        Map<String, Map<Integer, Double>> marks = new HashMap<>();

        for (DbSubject s : subjects) {
            List<DbTerm> terms = fetchTerms(s.code);
            if (terms.isEmpty()) {
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

            Map<Integer, Double> termMarks = new HashMap<>();
            for (DbTerm t : terms) termMarks.put(t.id, Math.random() * 100);
            marks.put(s.code, termMarks);
        }

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

            row[0] = s.name;
            row[1] = s.code;

            Map<Integer, Double> m = marks.getOrDefault(s.code, Collections.emptyMap());
            for (String lbl : dynamicAssessmentLabels) {
    Integer col = labelToColumnIndex.get(lbl);
    if (col == null) continue;
    Integer tid = subjectLabelTermIds.getOrDefault(s.code, Collections.emptyMap()).get(lbl);
    Double val = (tid != null) ? m.getOrDefault(tid, 0.0) : 0.0;

    // Default 0 for 3rd and 4th terms
    if (lbl.toLowerCase().contains("term 3") || lbl.toLowerCase().contains("term 4")) {
        val = 0.0;
    }

    row[col] = Double.valueOf(gradeFmt.format(val));
}


           model.addRow(row);
int newRow = model.getRowCount() - 1;

// --- Compute and format final grade ---
double finalVal = computeFinalForRow(newRow, s.code);
model.setValueAt(Double.valueOf(gradeFmt.format(finalVal)), newRow, row.length - 1);

        }
    }

    private void setColumnWidths() {
        if (table.getColumnModel().getColumnCount() == 0) return;

    TableColumnModel colModel = table.getColumnModel();

    // First column (Subject)
    TableColumn col0 = colModel.getColumn(0);
    col0.setPreferredWidth(500);  // width in pixels
    col0.setMinWidth(200);
    col0.setMaxWidth(500);
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

    private DefaultTableModel createModel(String[] columns){
        return new DefaultTableModel(null,columns){
            @Override public boolean isCellEditable(int row,int column){
                return column>1 && column<getColumnCount()-1;
            }
            @Override public Class<?> getColumnClass(int columnIndex){
                if(columnIndex>1 && columnIndex<getColumnCount()-1) return Number.class;
                return Object.class;
            }
        };
    }

    private double getNullableDouble(ResultSet rs, String col) throws SQLException {
        double d = rs.getDouble(col);
        return rs.wasNull() ? 0 : d;
    }

    private String safe(String s){ return s==null?"":s; }

    // ---------------- Models ----------------
    private static class DbSubject {
        String code,name; DbSubject(String c,String n){code=c;name=n;}
    }
    private static class DbTerm { int id; String label; double weight; DbTerm(int i,String l,double w){id=i;label=l;weight=w;} }

    private static class FinalGradeRenderer extends DefaultTableCellRenderer{
        @Override public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            JLabel lbl=(JLabel)super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            if(value instanceof Number){
                double g=((Number)value).doubleValue();
                if(g>=75) lbl.setForeground(new Color(0,128,0));
                else if(g>=50) lbl.setForeground(new Color(255,140,0));
                else lbl.setForeground(Color.RED);
                lbl.setText(String.format("%.1f", g));
            }
            return lbl;
        }
    }

    private static class NumericEditor extends DefaultCellEditor {
        NumericEditor(){super(new JTextField()); ((JTextField)getComponent()).setHorizontalAlignment(SwingConstants.CENTER);}
        @Override public Object getCellEditorValue(){ try{return Double.parseDouble(((JTextField)getComponent()).getText());}catch(Exception e){return 0.0;}}
    }
}
