import javax.swing.*;
import java.awt.*;

public class app {
    static JFrame frame;
    static ImageIcon pacmanIcon;

    public static void main(String[] args) {
        // Load gambar pacman saat aplikasi mulai
        pacmanIcon = loadResizedIcon("./pacmanRight.png", 60, 60);

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Pacman Java");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            showMainMenu();  // Tampilkan menu utama
        });
    }

    public static void showMainMenu() {
        if (frame.getContentPane() != null)
            frame.getContentPane().removeAll();

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PACMAN JAVA");
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tambahkan gambar pacman di bawah teks judul
        JLabel pacmanImgLabel = new JLabel(pacmanIcon);
        pacmanImgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tombol menu
        JButton level1Btn = new JButton("Level 1");
        JButton level2Btn = new JButton("Level 2");
        JButton quitBtn = new JButton("Keluar");

        level1Btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        level2Btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        level1Btn.setMaximumSize(new Dimension(200, 40));
        level2Btn.setMaximumSize(new Dimension(200, 40));
        quitBtn.setMaximumSize(new Dimension(200, 40));

        level1Btn.addActionListener(e -> showGamePanel(1));
        level2Btn.addActionListener(e -> showGamePanel(2));
        quitBtn.addActionListener(e -> System.exit(0));

        // Susun elemen UI
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(pacmanImgLabel); // Tambahkan gambar di sini
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(level1Btn);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(level2Btn);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(quitBtn);

        frame.setContentPane(mainPanel);
        frame.revalidate(); // Pastikan tampilan diperbarui
        frame.repaint();

        frame.pack();
        frame.setSize(640, 480); // Ukuran menu utama
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        System.out.println("Menu utama ditampilkan kembali.");
    }

    public static void showGamePanel(int level) {
        pacman gamePanel = new pacman(level);
        frame.getContentPane().removeAll();
        frame.setContentPane(gamePanel);
        frame.revalidate();
        frame.repaint();
        frame.pack();
        frame.setSize(gamePanel.getPreferredSize()); // agar sesuai ukuran map
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow(); // memastikan key listener aktif
    }

    // Method untuk me-resize gambar icon
    public static ImageIcon loadResizedIcon(String path, int width, int height) {
        try {
            ImageIcon rawIcon = new ImageIcon(app.class.getResource(path));
            Image scaled = rawIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Gagal load gambar: " + path);
            return new ImageIcon(); // placeholder kosong
        }
    }
}
