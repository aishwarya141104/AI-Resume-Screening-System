import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ResumeScreenUI extends JFrame {

    private ArrayList<Candidate> candidates = new ArrayList<>();

    public ResumeScreenUI() {

        setTitle("AI Resume Screening System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GradientPanel background = new GradientPanel();
        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(550, 350));
        card.setBackground(new Color(255, 255, 255, 220));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Resume Screening System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Upload Resume PDF");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton uploadButton = new JButton("Upload Resume");
        uploadButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        uploadButton.setMaximumSize(new Dimension(220, 50));
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(uploadButton);
        card.add(Box.createVerticalGlue());

        background.add(card);
        add(background);

        uploadButton.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();

                try {

                    PDDocument doc = Loader.loadPDF(file);
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(doc);
                    doc.close();

                    // Extract Name
                    String name = "Unknown";
                    for (String line : text.split("\\n")) {
                        if (!line.trim().isEmpty()) {
                            name = line.trim();
                            break;
                        }
                    }

                    // Extract Email
                    String email = "Not Found";

                    Matcher m = Pattern.compile(
                            "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
                    ).matcher(text);

                    if (m.find()) {
                        email = m.group();
                    }

                    // Skill Matching
                    String lower = text.toLowerCase();

                    String[] keywords = {
                            "java",
                            "python",
                            "html",
                            "css",
                            "javascript",
                            "power bi",
                            "figma",
                            "c++",
                            "mysql"
                    };

                    int score = 0;
                    StringBuilder skills = new StringBuilder();

                    for (String k : keywords) {
                        if (lower.contains(k)) {
                            score += 10;
                            skills.append("• ").append(k).append("\n");
                        }
                    }

                    Candidate candidate =
                            new Candidate(name, email, score);

                    candidates.add(candidate);

                    candidates.sort((a, b) -> b.score - a.score);

                    StringBuilder ranking = new StringBuilder();

                    int rank = 1;

                    for (Candidate c : candidates) {
                        ranking.append(rank++)
                                .append(". ")
                                .append(c.name)
                                .append(" - ")
                                .append(c.score)
                                .append("/90\n");
                    }

                    String report =
                            "Name: " + name +
                                    "\nEmail: " + email +
                                    "\n\nSkills:\n" + skills +
                                    "\nScore: " + score + "/90" +
                                    "\n\n===== CANDIDATE RANKING =====\n" +
                                    ranking;

                    // Save Report
                    JFileChooser saveChooser = new JFileChooser();
                    saveChooser.setSelectedFile(
                            new File("Resume_Report.txt"));

                    if (saveChooser.showSaveDialog(this)
                            == JFileChooser.APPROVE_OPTION) {

                        FileWriter writer =
                                new FileWriter(
                                        saveChooser.getSelectedFile());

                        writer.write(report);
                        writer.close();
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            report,
                            "Resume Screening Result",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception ex) {

                    ex.printStackTrace();

                    JOptionPane.showMessageDialog(
                            this,
                            "Error Reading PDF File!"
                    );
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ResumeScreenUI::new);
    }
}