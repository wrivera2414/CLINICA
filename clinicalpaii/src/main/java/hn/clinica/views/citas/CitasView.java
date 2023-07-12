package hn.clinica.views.citas;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import hn.clinica.data.entity.Citas;
import hn.clinica.data.service.CitasService;
import hn.clinica.views.MainLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Citas")
@Route(value = "citas/:citasID?/:action?(edit)", layout = MainLayout.class)
public class CitasView extends Div implements BeforeEnterObserver {

    private final String CITAS_ID = "citasID";
    private final String CITAS_EDIT_ROUTE_TEMPLATE = "citas/%s/edit";

    private final Grid<Citas> grid = new Grid<>(Citas.class, false);

    private TextField idcita;
    private DateTimePicker fecha;
    private TextField paciente;
    private TextField direccion;
    private TextField telefono;
    private TextArea detalle;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");

    private final BeanValidationBinder<Citas> binder;

    private Citas citas;

    //private final CitasService citasService;

    public CitasView() {
    //public CitasView(CitasService citasService) {
        //this.citasService = citasService;
        addClassNames("citas-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("idcita").setAutoWidth(true);
        grid.addColumn("fecha").setAutoWidth(true);
        grid.addColumn("paciente").setAutoWidth(true);
        grid.addColumn("direccion").setAutoWidth(true);
        grid.addColumn("telefono").setAutoWidth(true);
        grid.addColumn("detalle").setAutoWidth(true);
        /*grid.setItems(query -> citasService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());*/
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
        	clearForm();
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CITAS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CitasView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Citas.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(idcita).withConverter(new StringToIntegerConverter("Unicamente son permitidos Numeros")).bind("idcita");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.citas == null) {
                    this.citas = new Citas();
                }
                binder.writeBean(this.citas);
                //citasService.update(this.citas);
                clearForm();
                refreshGrid();
                Notification.show("Registro actualizado con exito");
                UI.getCurrent().navigate(CitasView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error al actualizar los datos. Alguien más ha actualizado el registro mientras estabas haciendo cambios.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Compruebe que los valores sean correctos");
            }
        });
        
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> citasId = event.getRouteParameters().get(CITAS_ID).map(Long::parseLong);
        if (citasId.isPresent()) {
           /* Optional<Citas> citasFromBackend = citasService.get(citasId.get());
            if (citasFromBackend.isPresent()) {
                populateForm(citasFromBackend.get());
            } else {
                Notification.show(String.format("No se encontro la cita solicitada, ID = %s", citasId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CitasView.class);
            }*/
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        idcita = new TextField("Numero de Cita");
        fecha = new DateTimePicker("Fecha");
       
        fecha.setLabel("Fecha y hora de la cita");
        fecha.setStep(Duration.ofMinutes(30));
        fecha.setHelperText("Elija en rango de 7 dias de 8AM/6PM");
        fecha.setAutoOpen(true);
        fecha.setMin(LocalDateTime.now());
        fecha.setMax(LocalDateTime.now().plusDays(7));
        fecha.setValue(LocalDateTime.now().plusDays(0));
        fecha.addValueChangeListener(event -> {
            LocalDateTime value = event.getValue();
            String errorMessage = null;
            if (value != null) {
                if (value.compareTo(fecha.getMin()) < 8) {
                    errorMessage = "Demaciado Temprano, Elija Otra Fecha y hora";
                } else if (value.compareTo(fecha.getMax()) > 16) {
                    errorMessage = "Demaciado Tarde, Elija Otra Fecha y hora";
                }
            }
            fecha.setErrorMessage(errorMessage);
        });
        add(fecha);
        paciente = new TextField("Paciente");
        paciente.setPrefixComponent(VaadinIcon.USER.create());
        direccion = new TextField("Direccion");
        direccion.setPrefixComponent(VaadinIcon.LOCATION_ARROW.create());
        telefono = new TextField("Telefono");
        telefono.setPrefixComponent(VaadinIcon.PHONE_LANDLINE.create());
        detalle = new TextArea("Detalle");
        detalle.setLabel("Comentario");
        detalle.setMaxLength(140);
        detalle.setValueChangeMode(ValueChangeMode.EAGER);
        detalle.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + (140));
        });
        detalle.setValue("Detalle de la cita");
        add(detalle);
        formLayout.add(idcita, fecha, paciente, direccion, telefono, detalle);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Citas value) {
        this.citas = value;
        binder.readBean(this.citas);

    }
}
