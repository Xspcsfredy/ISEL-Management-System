package tps.tp4;

import javax.swing.*;

// Ponto de entrada da interface grafica Swing da Parte B.
// Esta classe so arranca a janela de login.
public class AppISELGui {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Usa o aspeto normal do Windows para a app ficar integrada no sistema.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Se falhar o look and feel do sistema, prossegue na mesma
            }

            SistemaAcademicoController controller = new SistemaAcademicoController();
            if (controller.getStartupWarning() != null && !controller.getStartupWarning().isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        controller.getStartupWarning(),
                        "Aviso de arranque",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            showLogin(controller);
        });
    }

    private static void showLogin(SistemaAcademicoController controller) {
        LoginDialog loginWindow = new LoginDialog(controller);
        loginWindow.setVisible(true);
    }
}
