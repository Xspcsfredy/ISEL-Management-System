package tps.tp4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

// Janela inicial para registo e autenticacao.
// Aqui o utilizador entra com username e password antes de abrir o sistema principal.
public class LoginDialog extends JFrame {

    // Cores e fontes do ecrã de login, para ficar igual ao mockup.
    private static final Color BACKGROUND_COLOR = new Color(0xFF3C28);
    private static final Color ACCENT_COLOR = new Color(0x5F1437);
    private static final Color HEADER_COLOR = ACCENT_COLOR;
    private static final Color ORANGE_TEXT = new Color(0xFF4B2E);
    private static final Color FIELD_COLOR = new Color(0xFFF7F4);
    private static final Color FIELD_BORDER_COLOR = ACCENT_COLOR;
    private static final Color TITLE_COLOR = ORANGE_TEXT;
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 58);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 26);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 28);

    private final SistemaAcademicoController controller;
    private final RoundedTextField usernameField;
    private final RoundedPasswordField passwordField;
    private boolean authenticated;

    public LoginDialog(SistemaAcademicoController controller) {
        super("Iniciar Sessão");
        this.controller = controller;
        this.usernameField = new RoundedTextField(24);
        this.passwordField = new RoundedPasswordField(24);
        this.authenticated = false;

        // O icon da janela e da taskbar.
        setIconImage(AppAssets.loadAppIconImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        setContentPane(new LoginBackgroundPanel());
        setSize(1400, 850);
        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);

        // Se o utilizador fechar a janela sem entrar, a aplicacao termina.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (!authenticated) {
                    System.exit(0);
                }
            }
        });

        buildUi();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private void buildUi() {
        LoginBackgroundPanel background = (LoginBackgroundPanel) getContentPane();
        background.setLayout(new GridBagLayout());

        // O conteudo principal fica num cartao branco, centrado em cima do fundo.
        background.add(createCardPanel(), new GridBagConstraints());
    }

    private JComponent createCardPanel() {
        RoundedCardPanel card = new RoundedCardPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(1180, 760));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Parte de cima: titulo e logo.
        card.add(createTopPanel(), BorderLayout.NORTH);
        // Parte do meio: campos de texto.
        card.add(createCenterPanel(), BorderLayout.CENTER);
        // Parte de baixo: botoes de login, registo e sair.
        card.add(createBottomPanel(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel createTopPanel() {
        JPanel panel = new HeaderBandPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(28, 34, 28, 34));

        // O titulo fica mesmo no centro da janela.
        JLabel title = new JLabel("BEM-VINDO(A)", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TITLE_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Espaço vazio igual ao logo para equilibrar o lado direito.
        JPanel leftSpacer = new JPanel();
        leftSpacer.setOpaque(false);
        leftSpacer.setPreferredSize(new Dimension(180, 160));

        panel.add(leftSpacer, BorderLayout.WEST);
        panel.add(title, BorderLayout.CENTER);
        panel.add(createLogoPanel(), BorderLayout.EAST);
        return panel;
    }

    private JComponent createLogoPanel() {
        // Aqui mostramos o logo grande do ISEL dentro da janela.
        JPanel logoPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 0));
                g2.setStroke(new BasicStroke(10f));
                g2.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(160, 160));

        ImageIcon logoIcon = AppAssets.loadLogoIcon();
        JLabel logo = new JLabel(scaleIcon(logoIcon, 120, 120));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logoPanel.add(logo);
        return logoPanel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 180, 20, 180));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createFieldBlock("Nome de Utilizador", usernameField));
        panel.add(Box.createVerticalStrut(30));
        panel.add(createFieldBlock("Password", passwordField));
        return panel;
    }

    private JPanel createFieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(LABEL_FONT);
        label.setForeground(LABEL_COLOR);
        label.setBorder(new EmptyBorder(0, 0, 6, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        field.setPreferredSize(new Dimension(900, 112));

        block.add(label);
        block.add(field);
        return block;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 220, 34, 220));

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 28, 0));
        buttonRow.setOpaque(false);

        RoundedButton loginButton = createActionButton("LOGIN");
        RoundedButton registerButton = createActionButton("REGISTAR");
        JButton exitButton = createTextButton("SAIR");

        loginButton.addActionListener(e -> doLogin());
        registerButton.addActionListener(e -> doRegister());
        exitButton.addActionListener(e -> {
            authenticated = false;
            dispose();
        });

        buttonRow.add(loginButton);
        buttonRow.add(registerButton);

        panel.add(buttonRow, BorderLayout.NORTH);

        JPanel exitRow = new JPanel(new BorderLayout());
        exitRow.setOpaque(false);
        exitRow.setBorder(new EmptyBorder(28, 140, 0, 140));
        exitRow.add(exitButton, BorderLayout.CENTER);
        panel.add(exitRow, BorderLayout.SOUTH);
        return panel;
    }

    private RoundedButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(TITLE_COLOR);
        button.setBackground(ACCENT_COLOR);
        button.setPreferredSize(new Dimension(340, 92));
        return button;
    }

    // Botao simples sem rebordo, usado no "sair" para ficar mais leve.
    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 26));
        button.setForeground(TITLE_COLOR);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void doLogin() {
        // Tenta entrar no sistema com as credenciais escritas.
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            if (controller.login(username, password)) {
                authenticated = true;
                // Quando o login corre bem, abrimos a janela principal e fechamos esta.
                MainFrame mainFrame = new MainFrame(controller);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
                dispose();
            }
        } catch (AcademicoException ex) {
            showMessage("Falha no login", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doRegister() {
        // Cria um utilizador novo e guarda logo no XML.
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            controller.registerUser(username, password);
            showMessage("Registo", "Utilizador registado com sucesso.", JOptionPane.INFORMATION_MESSAGE);
        } catch (AcademicoException ex) {
            showMessage("Registo", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // Painel de fundo com a cor principal do login.
    private static final class LoginBackgroundPanel extends JPanel {
        private LoginBackgroundPanel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            Image backgroundImage = AppAssets.loadOptionalImage("isel_fundo.png");
            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.setColor(new Color(255, 255, 255, 70));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    // Cartao central branco com sombra suave.
    private static final class RoundedCardPanel extends JPanel {
        RoundedCardPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 38));
            g2.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 38, 38);
            g2.setColor(new Color(255, 250, 250, 240));
            g2.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 38, 38);
            g2.dispose();
        }
    }

    // Faixa de cima do cartão de login, com os cantos de cima arredondados.
    private static final class HeaderBandPanel extends JPanel {
        HeaderBandPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int arc = 42;

            g2.setColor(HEADER_COLOR);
            g2.fillRoundRect(0, 0, width - 1, height - 1, arc, arc);

            // O roundRect também arredonda em baixo; estes blocos "cortam" isso para a base ficar reta.
            int cut = arc / 2 + 2;
            g2.setColor(HEADER_COLOR);
            g2.fillRect(0, height - cut, cut, cut);
            g2.fillRect(width - cut, height - cut, cut, cut);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Campo de texto com cantos arredondados para ficar mais parecido ao mockup.
    private static final class RoundedTextField extends JTextField {
        RoundedTextField(int columns) {
            super(columns);
            setFont(BUTTON_FONT);
            setForeground(ACCENT_COLOR);
            setCaretColor(ACCENT_COLOR);
            setBorder(new EmptyBorder(18, 28, 18, 28));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_COLOR);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_BORDER_COLOR);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));
            g2.dispose();
        }
    }

    // Campo de password com o mesmo estilo arredondado do username.
    private static final class RoundedPasswordField extends JPasswordField {
        RoundedPasswordField(int columns) {
            super(columns);
            setFont(BUTTON_FONT);
            setForeground(ACCENT_COLOR);
            setCaretColor(ACCENT_COLOR);
            setBorder(new EmptyBorder(18, 28, 18, 28));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_COLOR);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_BORDER_COLOR);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 30, 30));
            g2.dispose();
        }
    }

    // Botao grande arredondado para dar um aspeto mais moderno.
    private static final class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(18, 24, 18, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 40, 40));
            if (getModel().isArmed() || getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 40, 40));
            }
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 40, 40));
            g2.dispose();
        }
    }
}
