package tps.tp4;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Janela principal da interface Swing, organizada em MVC.
// Aqui ficam os separadores e os botoes principais para gerir a aplicacao.
public class MainFrame extends JFrame {

    // Nomes das secoes principais do menu.
    private static final String CARD_RESUMO = "Resumo";
    private static final String CARD_PESSOAS = "Pessoas";
    private static final String CARD_ACADEMICO = "Academico";
    private static final String CARD_XPATH = "XPath";
    private static final String CARD_PERSISTENCIA = "Persistencia";

    // Icones da toolbar principal.
    private static final String ICON_GUARDAR = "toolbar_guardar.png";
    private static final String ICON_RECARREGAR = "toolbar_recarregar.png";
    private static final String ICON_SAIR = "toolbar_sair.png";

    private static final Color APP_BACKGROUND = new Color(0xD9D9D9);
    private static final Color PURPLE = new Color(0x5F1437);
    private static final Color PANEL_WHITE = new Color(0xFFF9F7);
    private static final Color ORANGE_TEXT = new Color(0xFF3C28);

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }

    private final SistemaAcademicoController controller;
    private final JLabel statusLabel;
    private final JTextArea summaryArea;
    private final JTextArea alunosArea;
    private final JTextArea docentesArea;
    private final JTextArea cursosArea;
    private final JTextArea ucsArea;
    private final JTextArea turmasArea;
    private final JTextArea inscricoesArea;
    private final JTextArea avaliacoesArea;
    private final JTextArea relatorioArea;
    private final JTextArea xpathAlunosArea;
    private final JTextArea xpathMediaArea;
    private final JLabel sessionLabel;
    private final CardLayout mainCardsLayout;
    private final JPanel mainCardsPanel;
    private final Map<String, JButton> sectionButtons;

    public MainFrame(SistemaAcademicoController controller) {
        super("MENU DO SISTEMA ACADÉMICO DO ISEL");
        this.controller = controller;
        this.statusLabel = new JLabel("Pronto");
        this.summaryArea = createTextArea();
        this.alunosArea = createTextArea();
        this.docentesArea = createTextArea();
        this.cursosArea = createTextArea();
        this.ucsArea = createTextArea();
        this.turmasArea = createTextArea();
        this.inscricoesArea = createTextArea();
        this.avaliacoesArea = createTextArea();
        this.relatorioArea = createTextArea();
        this.xpathAlunosArea = createTextArea();
        this.xpathMediaArea = createTextArea();
        this.sessionLabel = createSessionLabel();
        this.mainCardsLayout = new CardLayout();
        this.mainCardsPanel = new JPanel(mainCardsLayout);
        this.sectionButtons = new LinkedHashMap<>();

        // Usa o mesmo icon da aplicacao para a barra de titulo e para a taskbar.
        AppAssets.applyAppIcon(this);
        buildFrame();
        showSection(CARD_RESUMO);
        refreshAll();
    }

    private void buildFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1300, 860);
        setMinimumSize(new Dimension(1180, 760));
        setLocationRelativeTo(null);
        setContentPane(createRootPanel());
    }

    private JToolBar createToolbar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        bar.setOpaque(true);
        bar.setBackground(PURPLE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));
        bar.setBorderPainted(true);
        bar.setRollover(false);

        bar.add(createToolbarActionButton("GUARDAR", ICON_GUARDAR, () -> executeSimple("XML guardado", () -> controller.save(), this::refreshAll)));
        bar.add(createToolbarActionButton("RECARREGAR", ICON_RECARREGAR, () -> executeSimple("XML recarregado", () -> controller.reload(), this::refreshAll)));
        bar.add(createToolbarActionButton("SAIR", ICON_SAIR, () -> {
            try {
                controller.save();
            } catch (AcademicoException ex) {
                showError("Nao foi possivel guardar antes de sair.", ex);
            }
            dispose();
        }));
        bar.add(Box.createHorizontalGlue());
        bar.add(sessionLabel);
        return bar;
    }

    private JComponent createRootPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(APP_BACKGROUND);
        root.add(createToolbar(), BorderLayout.NORTH);
        root.add(createWorkspacePanel(), BorderLayout.CENTER);
        root.add(createStatusBar(), BorderLayout.SOUTH);
        return root;
    }

    private JComponent createWorkspacePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        RoundedPanel mainPanel = new RoundedPanel(PANEL_WHITE);
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.add(createSectionHeader(), BorderLayout.NORTH);
        mainPanel.add(createMainCards(), BorderLayout.CENTER);

        wrapper.add(mainPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent createSectionHeader() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 18, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        panel.add(createSectionButton("RESUMO", CARD_RESUMO));
        panel.add(createSectionButton("PESSOAS", CARD_PESSOAS));
        panel.add(createSectionButton("ACADEMICO", CARD_ACADEMICO));
        panel.add(createSectionButton("XPATH", CARD_XPATH));
        panel.add(createSectionButton("PERSISTENCIA", CARD_PERSISTENCIA));

        return panel;
    }

    private JComponent createMainCards() {
        mainCardsPanel.setOpaque(false);
        mainCardsPanel.add(createSummaryPanel(), CARD_RESUMO);
        mainCardsPanel.add(createPessoasPanel(), CARD_PESSOAS);
        mainCardsPanel.add(createAcademicoPanel(), CARD_ACADEMICO);
        mainCardsPanel.add(createXPathPanel(), CARD_XPATH);
        mainCardsPanel.add(createPersistenciaPanel(), CARD_PERSISTENCIA);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panel.add(mainCardsPanel, BorderLayout.CENTER);
        return panel;
    }

    private Component createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        panel.setBackground(APP_BACKGROUND);
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    private JButton createToolbarActionButton(String text, String iconFileName, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(ORANGE_TEXT);
        button.setBackground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(6);
        button.setPreferredSize(new Dimension(132, 72));

        ImageIcon icon = AppAssets.loadOptionalIcon(iconFileName);
        if (icon != null) {
            // Tamanho dos icones
            button.setIcon(scaleIcon(icon, 28, 28));
        }

        button.addActionListener(e -> action.run());
        return button;
    }

    private JButton createSectionButton(String text, String cardName) {
        SectionButton button = new SectionButton(text);
        button.addActionListener(e -> showSection(cardName));
        sectionButtons.put(cardName, button);
        return button;
    }

    private JButton createAccentActionButton(String text) {
        return new AccentButton(text);
    }

    private JLabel createSessionLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(ORANGE_TEXT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 14));
        updateSessionLabel(label);
        return label;
    }

    private void updateSessionLabel(JLabel label) {
        String username = controller.getCurrentUsername();
        label.setText(username == null || username.isBlank() ? "Sessão: utilizador" : "Sessão: " + username);
    }

    private void showSection(String sectionName) {
        mainCardsLayout.show(mainCardsPanel, sectionName);
        updateSectionButtons(sectionName);
        log(sectionName);
    }

    private void updateSectionButtons(String selectedSection) {
        for (Map.Entry<String, JButton> entry : sectionButtons.entrySet()) {
            boolean selected = entry.getKey().equals(selectedSection);
            JButton button = entry.getValue();
            if (button instanceof SectionButton sectionButton) {
                sectionButton.setSelectedState(selected);
            }
        }
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        summaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        JButton refresh = createAccentActionButton("Atualizar resumo");
        refresh.addActionListener(e -> refreshSummary());

        panel.add(section("Estado atual", new JScrollPane(summaryArea)), BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPessoasPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Alunos", createAlunosPanel());
        tabs.addTab("Docentes", createDocentesPanel());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAcademicoPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Cursos", createCursosPanel());
        tabs.addTab("UCs", createUcsPanel());
        tabs.addTab("Turmas", createTurmasPanel());
        tabs.addTab("Inscricoes", createInscricoesPanel());
        tabs.addTab("Avaliacoes", createAvaliacoesPanel());
        tabs.addTab("Relatorio", createRelatorioPanel());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createXPathPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(createXPathTurmaPanel());
        panel.add(createXPathMediaPanel());
        return panel;
    }

    private JPanel createPersistenciaPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JTextArea info = createTextArea();
        info.setText("XML: " + controller.getXmlInfo() + "\nDTD: " + controller.getDtdPath().toAbsolutePath());

        JButton save = createAccentActionButton("Guardar XML");
        JButton reload = createAccentActionButton("Recarregar XML");

        save.addActionListener(e -> executeSimple("XML guardado", () -> controller.save(), this::refreshAll));
        reload.addActionListener(e -> executeSimple("XML recarregado", () -> controller.reload(), this::refreshAll));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(save);
        buttons.add(reload);

        panel.add(section("Informacao", new JScrollPane(info)), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAlunosPanel() {
        JTextField numero = field(8);
        JTextField nome = field(20);
        JTextField email = field(22);
        JTextField curso = field(10);
        JTextField ano = field(4);

        JButton add = createAccentActionButton("Registar");
        JButton remove = createAccentActionButton("Remover");
        JButton refresh = createAccentActionButton("Atualizar lista");

        add.addActionListener(e -> executeSimple("Aluno registado", () -> controller.registarAluno(parseInt(numero, "numero"), nome.getText(), email.getText(), curso.getText(), parseInt(ano, "ano")), this::refreshAll));
        remove.addActionListener(e -> executeSimple("Aluno removido", () -> controller.removerAluno(parseInt(numero, "numero")), this::refreshAll));
        refresh.addActionListener(e -> refreshAlunos());

        JPanel form = gridForm(new String[]{"Numero", "Nome", "Email", "Curso", "Ano"}, numero, nome, email, curso, ano);
        JPanel buttons = buttonRow(add, remove, refresh);

        JPanel panel = baseSection(form, buttons, new JScrollPane(alunosArea));
        return panel;
    }

    private JPanel createDocentesPanel() {
        JTextField id = field(8);
        JTextField nome = field(20);
        JTextField email = field(22);
        JTextField departamento = field(16);

        JButton add = createAccentActionButton("Registar");
        JButton remove = createAccentActionButton("Remover");
        JButton refresh = createAccentActionButton("Atualizar lista");

        add.addActionListener(e -> executeSimple("Docente registado", () -> controller.registarDocente(parseInt(id, "id"), nome.getText(), email.getText(), departamento.getText()), this::refreshAll));
        remove.addActionListener(e -> executeSimple("Docente removido", () -> controller.removerDocente(parseInt(id, "id")), this::refreshAll));
        refresh.addActionListener(e -> refreshDocentes());

        JPanel form = gridForm(new String[]{"ID", "Nome", "Email", "Departamento"}, id, nome, email, departamento);
        JPanel buttons = buttonRow(add, remove, refresh);

        return baseSection(form, buttons, new JScrollPane(docentesArea));
    }

    private JPanel createCursosPanel() {
        JTextField codigo = field(10);
        JTextField nome = field(24);
        JTextField duracao = field(6);

        JButton add = createAccentActionButton("Criar");
        JButton refresh = createAccentActionButton("Atualizar lista");

        add.addActionListener(e -> executeSimple("Curso criado", () -> controller.criarCurso(codigo.getText(), nome.getText(), parseInt(duracao, "duracao")), this::refreshAll));
        refresh.addActionListener(e -> refreshCursos());

        JPanel form = gridForm(new String[]{"Codigo", "Nome", "Duracao"}, codigo, nome, duracao);
        JPanel buttons = buttonRow(add, refresh);
        return baseSection(form, buttons, new JScrollPane(cursosArea));
    }

    private JPanel createUcsPanel() {
        JTextField codigoCurso = field(10);
        JTextField codigo = field(10);
        JTextField nome = field(24);
        JTextField ects = field(6);
        JTextField anoCurso = field(6);
        JTextField semestre = field(6);
        JTextField capacidade = field(6);

        JButton add = createAccentActionButton("Criar");
        JButton refresh = createAccentActionButton("Atualizar lista");

        add.addActionListener(e -> executeSimple("UC criada", () -> controller.criarUc(codigo.getText(), nome.getText(), parseInt(ects, "ects"), parseInt(semestre, "semestre"), parseInt(anoCurso, "ano"), codigoCurso.getText(), parseInt(capacidade, "capacidade")), this::refreshAll));
        refresh.addActionListener(e -> refreshUcs());

        JPanel form = gridForm(
                new String[]{"Curso", "Codigo", "Nome", "ECTS", "Ano Curso", "Semestre", "Capacidade"},
                codigoCurso, codigo, nome, ects, anoCurso, semestre, capacidade);
        JPanel buttons = buttonRow(add, refresh);
        return baseSection(form, buttons, new JScrollPane(ucsArea));
    }

    private JPanel createTurmasPanel() {
        JTextField id = field(10);
        JTextField codigoUc = field(10);
        JTextField anoInicio = field(8);
        JTextField semestre = field(6);
        JTextField capacidade = field(6);
        JTextField idDocente = field(8);

        JButton create = createAccentActionButton("Criar turma");
        JButton assign = createAccentActionButton("Atribuir docente");
        JButton refresh = createAccentActionButton("Atualizar lista");

        create.addActionListener(e -> executeSimple("Turma criada", () -> controller.criarTurma(id.getText(), codigoUc.getText(), parseInt(anoInicio, "ano inicio"), parseInt(semestre, "semestre"), parseInt(capacidade, "capacidade")), this::refreshAll));
        assign.addActionListener(e -> executeSimple("Docente atribuido", () -> controller.atribuirDocenteTurma(id.getText(), parseInt(idDocente, "id docente")), this::refreshAll));
        refresh.addActionListener(e -> refreshTurmas());

        JPanel form = gridForm(new String[]{"ID Turma", "UC", "Ano Inicio", "Semestre", "Capacidade", "ID Docente"}, id, codigoUc, anoInicio, semestre, capacidade, idDocente);
        JPanel buttons = buttonRow(create, assign, refresh);
        return baseSection(form, buttons, new JScrollPane(turmasArea));
    }

    private JPanel createInscricoesPanel() {
        JTextField numeroAluno = field(8);
        JTextField codigoUc = field(10);
        JTextField idTurma = field(10);

        JButton enroll = createAccentActionButton("Inscrever");
        JButton cancel = createAccentActionButton("Anular");
        JButton refresh = createAccentActionButton("Atualizar lista");

        enroll.addActionListener(e -> executeSimple("Aluno inscrito", () -> controller.inscreverAluno(parseInt(numeroAluno, "numero aluno"), codigoUc.getText(), idTurma.getText()), this::refreshAll));
        cancel.addActionListener(e -> executeSimple("Inscricao anulada", () -> controller.anularInscricao(parseInt(numeroAluno, "numero aluno"), idTurma.getText()), this::refreshAll));
        refresh.addActionListener(e -> refreshInscricoes());

        JPanel form = gridForm(new String[]{"Aluno", "UC", "Turma"}, numeroAluno, codigoUc, idTurma);
        JPanel buttons = buttonRow(enroll, cancel, refresh);
        return baseSection(form, buttons, new JScrollPane(inscricoesArea));
    }

    private JPanel createAvaliacoesPanel() {
        JTextField numeroAluno = field(8);
        JTextField codigoUc = field(10);
        JTextField elemento = field(18);
        JTextField nota = field(6);
        JTextField peso = field(6);

        JButton add = createAccentActionButton("Lancar");
        JButton refresh = createAccentActionButton("Atualizar lista");

        add.addActionListener(e -> executeSimple("Avaliacao lancada", () -> controller.lancarAvaliacao(parseInt(numeroAluno, "numero aluno"), codigoUc.getText(), elemento.getText(), parseDouble(nota, "nota"), parseDouble(peso, "peso")), this::refreshAll));
        refresh.addActionListener(e -> refreshAvaliacoes());

        JPanel form = gridForm(new String[]{"Aluno", "UC", "Elemento", "Nota", "Peso"}, numeroAluno, codigoUc, elemento, nota, peso);
        JPanel buttons = buttonRow(add, refresh);
        return baseSection(form, buttons, new JScrollPane(avaliacoesArea));
    }

    private JPanel createRelatorioPanel() {
        JTextField codigoUc = field(10);
        JButton generate = createAccentActionButton("Gerar relatorio");
        JButton refresh = createAccentActionButton("Limpar");

        relatorioArea.setLineWrap(true);
        relatorioArea.setWrapStyleWord(true);

        generate.addActionListener(e -> {
            try {
                RelatorioUc report = controller.gerarRelatorioUc(codigoUc.getText());
                if (report == null) {
                    relatorioArea.setText("UC nao encontrada.");
                } else {
                    relatorioArea.setText(report.toString());
                }
                log("Relatorio de UC gerado");
            } catch (Exception ex) {
                showError("Relatorio", ex);
            }
        });
        refresh.addActionListener(e -> relatorioArea.setText(""));

        JPanel form = gridForm(new String[]{"Codigo UC"}, codigoUc);
        JPanel buttons = buttonRow(generate, refresh);
        return baseSection(form, buttons, new JScrollPane(relatorioArea));
    }

    private JPanel createXPathTurmaPanel() {
        JTextField idTurma = field(10);
        JButton run = createAccentActionButton("Executar");
        JButton clear = createAccentActionButton("Limpar");

        run.addActionListener(e -> {
            try {
                List<String> alunos = controller.consultarAlunosPorTurma(idTurma.getText());
                xpathAlunosArea.setText(alunos.isEmpty() ? "Sem alunos na turma." : String.join("\n", alunos));
                log("Consulta XPath por turma executada");
            } catch (Exception ex) {
                showError("XPath", ex);
            }
        });
        clear.addActionListener(e -> xpathAlunosArea.setText(""));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(titled("XPath - Alunos por turma"));
        panel.add(gridForm(new String[]{"ID Turma"}, idTurma), BorderLayout.NORTH);
        panel.add(section("Resultado", new JScrollPane(xpathAlunosArea)), BorderLayout.CENTER);
        panel.add(buttonRow(run, clear), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createXPathMediaPanel() {
        JTextField codigoUc = field(10);
        JButton run = createAccentActionButton("Executar");
        JButton clear = createAccentActionButton("Limpar");

        run.addActionListener(e -> {
            try {
                Double media = controller.consultarMediaUc(codigoUc.getText());
                xpathMediaArea.setText(media == null ? "Sem notas para a UC." : "Media ponderada: " + media);
                log("Consulta XPath de media executada");
            } catch (Exception ex) {
                showError("XPath", ex);
            }
        });
        clear.addActionListener(e -> xpathMediaArea.setText(""));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(titled("XPath - Media de notas"));
        panel.add(gridForm(new String[]{"Codigo UC"}, codigoUc), BorderLayout.NORTH);
        panel.add(section("Resultado", new JScrollPane(xpathMediaArea)), BorderLayout.CENTER);
        panel.add(buttonRow(run, clear), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel baseSection(JComponent form, JComponent buttons, JComponent center) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(section("Formulario", form), BorderLayout.NORTH);
        panel.add(section("Lista", center), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel gridForm(String[] labels, JTextField... fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            panel.add(new JLabel(labels[i] + ":"), c);
            c.gridx = 1;
            c.weightx = 1.0;
            panel.add(fields[i], c);
        }
        return panel;
    }

    private JPanel buttonRow(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }

    private JComponent section(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(titled(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private TitledBorder titled(String title) {
        return BorderFactory.createTitledBorder(title);
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea(12, 40);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JTextField field(int columns) {
        return new JTextField(columns);
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // Botao roxo arredondado para as acoes do menu principal.
    private static final class AccentButton extends JButton {
        AccentButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setForeground(ORANGE_TEXT);
            setBackground(PURPLE);
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(170, 42));
            setMargin(new Insets(8, 14, 8, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(PURPLE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            if (getModel().isRollover() || getModel().isArmed()) {
                g2.setColor(new Color(255, 255, 255, 28));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class SectionButton extends JButton {
        private boolean selected;

        SectionButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.BOLD, 18));
            setForeground(PURPLE);
            setBackground(new Color(0, 0, 0, 0));
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 58));
        }

        void setSelectedState(boolean selected) {
            this.selected = selected;
            setForeground(selected ? ORANGE_TEXT : PURPLE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected) {
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            } else {
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final Color fillColor;

        RoundedPanel(Color fillColor) {
            this.fillColor = fillColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 26, 26);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 16, getHeight() - 16, 26, 26);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private int parseInt(JTextField field, String nomeCampo) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Campo invalido: " + nomeCampo);
        }
    }

    private double parseDouble(JTextField field, String nomeCampo) {
        try {
            return Double.parseDouble(field.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Campo invalido: " + nomeCampo);
        }
    }

    private void executeSimple(String successMessage, ThrowingAction action, Runnable refresh) {
        try {
            action.run();
            if (refresh != null) {
                refresh.run();
            }
            refreshSummary();
            log(successMessage);
        } catch (Exception ex) {
            showError(successMessage, ex);
        }
    }

    private void showError(String title, Exception ex) {
        String message = ex.getMessage() == null ? ex.toString() : ex.getMessage();
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        log("Erro: " + message);
    }

    private void log(String message) {
        statusLabel.setText(message);
    }

    private void refreshAll() {
        refreshSummary();
        refreshAlunos();
        refreshDocentes();
        refreshCursos();
        refreshUcs();
        refreshTurmas();
        refreshInscricoes();
        refreshAvaliacoes();
    }

    private void refreshSummary() {
        summaryArea.setText(controller.getSummary());
    }

    private void refreshAlunos() {
        alunosArea.setText(formatList(controller.getSistema().listarAlunos()));
    }

    private void refreshDocentes() {
        StringBuilder sb = new StringBuilder();
        for (Pessoa pessoa : controller.getSistema().listarPessoas()) {
            if (pessoa instanceof Docente) {
                sb.append((Docente) pessoa).append('\n');
            }
        }
        docentesArea.setText(sb.length() == 0 ? "Sem docentes registados." : sb.toString());
    }

    private void refreshCursos() {
        cursosArea.setText(formatList(controller.getSistema().listarCursos()));
    }

    private void refreshUcs() {
        ucsArea.setText(formatList(controller.getSistema().listarUcs()));
    }

    private void refreshTurmas() {
        turmasArea.setText(formatList(controller.getSistema().listarTurmas()));
    }

    private void refreshInscricoes() {
        inscricoesArea.setText(formatList(controller.getSistema().getInscricoesInterno()));
    }

    private void refreshAvaliacoes() {
        avaliacoesArea.setText(formatList(controller.getSistema().getAvaliacoesInterno()));
    }

    private String formatList(List<?> items) {
        if (items.isEmpty()) {
            return "Sem dados.";
        }

        StringBuilder builder = new StringBuilder();
        for (Object item : items) {
            builder.append(item).append('\n');
        }
        return builder.toString();
    }
}
