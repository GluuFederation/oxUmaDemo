package org.xdi.uma.demo.rp.client;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;
import org.xdi.gwt.client.GwtUtils;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2013
 */

public class NewPhoneDialog extends Dialog {

    private final TextField m_textField = new TextField();

    public NewPhoneDialog() {
        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CLOSE);
        setHeadingText("Add phone");
        setHideOnButtonClick(true);
        setModal(true);
        setSize("300px", "250px");
        setWidget(createContent());
    }

    private Widget createContent() {
        final VerticalLayoutContainer c = new VerticalLayoutContainer();
        c.add(GwtUtils.createFieldLabel(m_textField, "Phone"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, RP.DEFAULT_MARGINS));
        return c;
    }

    public TextField getTextField() {
        return m_textField;
    }
}
