package lecturer;

import Database.dbconnection;
import Login.Login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pure Swing / IntelliJ version of UploadMarksExams.
 */
public class UploadMarksExams extends JFrame {

    private marks marks = new marks();
    private DefaultTableModel model;
    private int rowIndex;
    private final String currentUserId;

    // UI components
    private JTable marksTable;
    private JTextField Mark_ID, Lecture_ID, Student_ID,
            Quiz_1, Quiz_2, Quiz_3, Quiz_4,
            ass1, ass2, Mid_term, final_T, final_P;
    private JComboBox<String> Course_ID;
    private JButton addButton, updateButton, deleteButton, clearButton;

    public UploadMarksExams(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        Lecture_ID.setText(currentUserId);
        Lecture_ID.setEditable(false);
        tableViewMarks();
        SwingUtilities.invokeLater(() -> setComboBox(userId));
    }

    // ======= Initialize UI =======
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Upload Marks");
        setSize(1200, 750);
        setLocationRelativeTo(null);

        // sidebar
        JPanel sidebar = buildSidebar();

        // top header
        JLabel lblTop = new JLabel("ADD / UPDATE MARKS", SwingConstants.CENTER);
        lblTop.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        lblTop.setForeground(Color.WHITE);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        header.add(lblTop, BorderLayout.CENTER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(153, 153, 153));
        leftPanel.setLayout(null);

        int xLbl = 20, xFld = 150, wLbl = 120, wTxt = 160, h = 28, pad = 40, y = 30;

        leftPanel.add(label("Mark ID", xLbl, y));
        Mark_ID = field(xFld, y, wTxt);
        Mark_ID.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){Mark_ID.setText("");}
        });
        leftPanel.add(Mark_ID);

        y += pad;
        leftPanel.add(label("Lecture ID", xLbl, y));
        Lecture_ID = field(xFld, y, wTxt);
        Lecture_ID.setEditable(false);
        leftPanel.add(Lecture_ID);

        y += pad;
        leftPanel.add(label("Student ID", xLbl, y));
        Student_ID = field(xFld, y, wTxt);
        leftPanel.add(Student_ID);

        y += pad;
        leftPanel.add(label("Course ID", xLbl, y));
        Course_ID = new JComboBox<>();
        Course_ID.setBounds(xFld, y, wTxt, h);
        Course_ID.addActionListener(e -> applyCourseRules());
        leftPanel.add(Course_ID);

        y += pad;
        leftPanel.add(label("Quiz 1", xLbl, y));
        Quiz_1 = field(xFld, y, wTxt); leftPanel.add(Quiz_1);
        y += pad;
        leftPanel.add(label("Quiz 2", xLbl, y));
        Quiz_2 = field(xFld, y, wTxt); leftPanel.add(Quiz_2);
        y += pad;
        leftPanel.add(label("Quiz 3", xLbl, y));
        Quiz_3 = field(xFld, y, wTxt); leftPanel.add(Quiz_3);
        y += pad;
        leftPanel.add(label("Quiz 4", xLbl, y));
        Quiz_4 = field(xFld, y, wTxt); leftPanel.add(Quiz_4);
        y += pad;
        leftPanel.add(label("Assessment 1", xLbl, y));
        ass1 = field(xFld, y, wTxt); leftPanel.add(ass1);
        y += pad;
        leftPanel.add(label("Assessment 2", xLbl, y));
        ass2 = field(xFld, y, wTxt); leftPanel.add(ass2);
        y += pad;
        leftPanel.add(label("Mid Term", xLbl, y));
        Mid_term = field(xFld, y, wTxt); leftPanel.add(Mid_term);
        y += pad;
        leftPanel.add(label("Final Theory", xLbl, y));
        final_T = field(xFld, y, wTxt); leftPanel.add(final_T);
        y += pad;
        leftPanel.add(label("Final Practical", xLbl, y));
        final_P = field(xFld, y, wTxt); leftPanel.add(final_P);

        // Action buttons
        addButton = button("Add", 20, 640);
        updateButton = button("Update", 110, 640);
        deleteButton = button("Delete", 210, 640);
        clearButton = button("Clear", 310, 640);

        addButton.addActionListener(e -> onAdd());
        updateButton.addActionListener(e -> onUpdate());
        deleteButton.addActionListener(e -> onDelete());
        clearButton.addActionListener(e -> clearMarks());

        leftPanel.add(addButton);
        leftPanel.add(updateButton);
        leftPanel.add(deleteButton);
        leftPanel.add(clearButton);

        // Table section
        marksTable = new JTable(new DefaultTableModel(new Object[][]{},
                new String[]{"Mark ID","LectureID","Student ID","Course ID",
                        "Quiz 1","Quiz 2","Quiz 3","Quiz 4",
                        "Ass 1","Ass 2","Mid Term","Final T","Final P"}));
        marksTable.setRowHeight(25);
        marksTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        marksTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){onTableClick();}
        });
        JScrollPane scroll = new JScrollPane(marksTable);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(scroll, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(leftPanel, BorderLayout.CENTER);
        getContentPane().add(rightPanel, BorderLayout.EAST);
    }

    private JLabel label(String text,int x,int y){
        JLabel l=new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setBounds(x,y,120,30);
        return l;
    }
    private JTextField field(int x,int y,int w){
        JTextField f=new JTextField();
        f.setBounds(x,y,w,28);
        return f;
    }
    private JButton button(String text,int x,int y){
        JButton b=new JButton(text);
        b.setBounds(x,y,80,30);
        return b;
    }

    // sidebar builder
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(new Color(153,187,187));
        side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));
        JLabel logo=new JLabel(new ImageIcon(getClass().getResource("/SystemImages/resize_logo.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(logo);
        side.add(Box.createVerticalStrut(20));

        side.add(nav("Profile",e->open(new Lecture_profile(currentUserId))));
        side.add(nav("Course",e->open(new AddCourseMaterials(currentUserId))));
        side.add(nav("Marks",e->open(new UploadMarksExams(currentUserId))));
        side.add(nav("Student",e->open(new StudentDetails(currentUserId))));
        side.add(nav("Eligibility",e->open(new CAEligibility(currentUserId))));
        side.add(nav("GPA",e->open(new GPAcalculation(currentUserId))));
        side.add(nav("Grades & Marks",e->open(new GradePoint(currentUserId))));
        side.add(nav("Attendance",e->open(new Attendance(currentUserId))));
        side.add(nav("Medical",e->open(new MedicalLEC(currentUserId))));
        side.add(nav("Notices",e->open(new Notice(currentUserId))));

        JLabel logout=nav("LOGOUT",e->{
            if(JOptionPane.showConfirmDialog(this,"Logout?","Logout",
                    JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                new Login().setVisible(true);dispose();}
        });
        logout.setForeground(new Color(153,0,0));
        side.add(Box.createVerticalStrut(15));
        side.add(logout);
        return side;
    }

    private JLabel nav(String text,java.util.function.Consumer<MouseEvent> c){
        JLabel l=new JLabel(text,SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI",Font.BOLD,17));
        l.setMaximumSize(new Dimension(200,30));
        l.setBorder(BorderFactory.createMatteBorder(0,0,0,2,Color.BLACK));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e){c.accept(e);}});
        return l;
    }

    private void open(JFrame f){f.setVisible(true);dispose();}

    // ======= Behaviour / Data =======
    private void tableViewMarks(){
        marks.getMarksValues(marksTable,currentUserId);
        model =(DefaultTableModel)marksTable.getModel();
    }
    private void clearMarks(){
        Lecture_ID.setText(currentUserId);
        Student_ID.setText(""); Quiz_1.setText(""); Quiz_2.setText("");
        Quiz_3.setText(""); Quiz_4.setText("");
        ass1.setText(""); ass2.setText("");
        Mid_term.setText(""); final_T.setText(""); final_P.setText("");
        Mark_ID.setText("");
        marksTable.clearSelection();
        resetAllMarkFields();
    }
    private boolean isEmptyMarks(){
        if(Mark_ID.getText().isEmpty()){JOptionPane.showMessageDialog(this,"Mark ID empty");return false;}
        if(Lecture_ID.getText().isEmpty()){JOptionPane.showMessageDialog(this,"Lecture ID empty");return false;}
        if(Student_ID.getText().isEmpty()){JOptionPane.showMessageDialog(this,"Student ID empty");return false;}
        return true;
    }

    private void setComboBox(String userId){
        try(Connection con=dbconnection.getConnection();
            PreparedStatement ps=con.prepareStatement("select course_id from course where lec_id=?")){
            ps.setString(1,userId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){Course_ID.addItem(rs.getString(1));}
        }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage());}
    }

    private void onAdd(){
        if(isEmptyMarks()){
            marks.insert(Lecture_ID.getText(),Student_ID.getText(),
                    (String)Course_ID.getSelectedItem(),
                    parse(Quiz_1),parse(Quiz_2),parse(Quiz_3),parse(Quiz_4),
                    parse(ass1),parse(ass2),parse(Mid_term),parse(final_T),parse(final_P));
            tableViewMarks(); clearMarks();
        }
    }

    private void onUpdate(){
        if(isEmptyMarks()){
            marks.update(Mark_ID.getText(),Lecture_ID.getText(),Student_ID.getText(),
                    (String)Course_ID.getSelectedItem(),
                    parse(Quiz_1),parse(Quiz_2),parse(Quiz_3),parse(Quiz_4),
                    parse(ass1),parse(ass2),parse(Mid_term),parse(final_T),parse(final_P));
            clearMarks(); tableViewMarks();
        }
    }

    private void onDelete(){
        if(marks.delete(Mark_ID.getText())){ clearMarks(); tableViewMarks();}
        else JOptionPane.showMessageDialog(this,"Enter valid Mark ID!");
    }

    private float parse(JTextField f){
        String t=f.getText().trim();
        if(t.equalsIgnoreCase("N/A")||t.isEmpty())return 0;
        try{return Float.parseFloat(t);}catch(Exception ex){return 0;}
    }

    private void onTableClick(){
        model=(DefaultTableModel)marksTable.getModel();
        rowIndex=marksTable.getSelectedRow();
        if(rowIndex<0)return;
        Mark_ID.setText(val(0)); Lecture_ID.setText(val(1));
        Student_ID.setText(val(2)); Course_ID.setSelectedItem(val(3));
        Quiz_1.setText(val(4)); Quiz_2.setText(val(5)); Quiz_3.setText(val(6)); Quiz_4.setText(val(7));
        ass1.setText(val(8)); ass2.setText(val(9)); Mid_term.setText(val(10));
        final_T.setText(val(11)); final_P.setText(val(12));
    }
    private String val(int col){return model.getValueAt(rowIndex,col).toString();}

    // field enabling/disabling same as before
    private void resetAllMarkFields(){
        JTextField[] f={Quiz_1,Quiz_2,Quiz_3,Quiz_4,ass1,ass2,Mid_term,final_T,final_P};
        for(JTextField x:f)setFieldActive(x);
    }
    private void applyCourseRules(){
        resetAllMarkFields();
        String c=(String)Course_ID.getSelectedItem();
        if(c==null)return;
        switch(c){
            case "ict2113" -> setFieldsInactive(Quiz_4,ass1,ass2);
            case "ict2122" -> setFieldsInactive(final_P,ass2);
            case "ict2133" -> setFieldsInactive(Quiz_4,Mid_term);
            case "ict2142" -> setFieldsInactive(Quiz_1,Quiz_2,Quiz_3,Quiz_4,final_T,ass2);
            case "ict2152" -> setFieldsInactive(Quiz_4,Mid_term,final_P);
        }
    }
    private void setFieldsInactive(JTextField...f){for(JTextField x:f)setFieldInactive(x);}
    private void setFieldInactive(JTextField f){f.setEditable(false);f.setBackground(new Color(204,204,255));f.setText("N/A");}
    private void setFieldActive(JTextField f){f.setEditable(true);f.setBackground(Color.WHITE);f.setText("");}

    public static void main(String[] a){SwingUtilities.invokeLater(()->new UploadMarksExams("L001").setVisible(true));}
}