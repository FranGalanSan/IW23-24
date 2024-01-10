package com.easycall.project.views;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.easycall.project.data.user.User;
import com.easycall.project.sms.SMS;
import com.easycall.project.sms.SMSService;

@Route("DesgloseView")
public class DesgloseView extends VerticalLayout {

    private final SMSService smsService;
    private TextField numeroDestinatarioField;
    private TextField contenidoSMSField;
    private Button enviarSMSButton;
    private Grid<SMS> gridSMSEnviados;
    private Grid<SMS> gridSMSRecibidos;

    public DesgloseView(SMSService smsService) {
        this.smsService = smsService;
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            setupUI(currentUser);
            setupGrids(currentUser);
        } else {
            add(new Span("Usuario no encontrado o sesión no iniciada."));
        }
    }

    private void setupUI(User currentUser) {
        numeroDestinatarioField = new TextField("Número de Destinatario");
        contenidoSMSField = new TextField("Contenido del SMS");
        enviarSMSButton = new Button("Enviar SMS", e -> enviarSMS(currentUser));

        add(numeroDestinatarioField, contenidoSMSField, enviarSMSButton);
    }

    private void enviarSMS(User user) {
        String numero = numeroDestinatarioField.getValue();
        String contenido = contenidoSMSField.getValue();
        try {
            smsService.enviarSMS(user, numero, contenido);
            Notification.show("SMS enviado a " + numero);
        } catch (Exception e) {
            Notification.show("Error al enviar SMS: " + e.getMessage());
        }
    }

    private void setupGrids(User currentUser) {
        gridSMSEnviados = new Grid<>(SMS.class);
        gridSMSEnviados.setColumns("numeroDestinatario", "contenidoSMS");
        gridSMSEnviados.setItems(smsService.obtenerSMSEnviados(currentUser));
        add(gridSMSEnviados);

        gridSMSRecibidos = new Grid<>(SMS.class);
        gridSMSRecibidos.setColumns("numeroDestinatario", "contenidoSMS");
        gridSMSRecibidos.setItems(smsService.obtenerSMSRecibidos(currentUser.getPhone()));
        add(gridSMSRecibidos);
    }

    private User getCurrentUser() {
        return VaadinSession.getCurrent().getAttribute(User.class);
    }
}
