package org.xdi.uma.demo.rs.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;
import org.xdi.gwt.client.GwtUtils;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.gwt.ui.ProgressDialog;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2016
 */

public class RS implements EntryPoint {

    private static final Margins DEFAULT_MARGINS = new Margins(2, 2, 2, 2);

    private static final ServiceAsync SERVICE = GWT.create(Service.class);
    private static final TextArea TEXT_AREA = new TextArea();

    @Override
    public void onModuleLoad() {
        final Viewport viewport = new Viewport();
        viewport.add(createContent(), new MarginData(0));
        RootLayoutPanel.get().add(viewport);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                refresh();
            }
        });
    }

    private ContentPanel createContent() {
        TEXT_AREA.setReadOnly(true);

        final TextButton refreshButton = new TextButton("Refresh");
        refreshButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                refresh();
            }
        });

        final TextButton newPatButton = new TextButton("Obtain new PAT token");
        newPatButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final ProgressDialog progressDialog = new ProgressDialog("Perform operation...");
                progressDialog.show();
                getService().obtainNewPat(new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        progressDialog.hide();
                        handleException(caught);
                        GwtUtils.showError("Unable to obtain new PAT token.");
                        refresh();
                    }

                    @Override
                    public void onSuccess(String result) {
                        progressDialog.hide();
                        GwtUtils.showInformation("PAT token is obtained successfully.");
                        refresh();
                    }
                });
            }
        });

        final TextButton clearButton = new TextButton("Clear");
        clearButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                getService().clearLogs(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable p_throwable) {
                        handleException(p_throwable);
                        GwtUtils.showError("Unable to clear logs.");
                    }

                    @Override
                    public void onSuccess(Void p_void) {
                        TEXT_AREA.setValue("");
                    }
                });
            }
        });

        final HBoxLayoutContainer h = new HBoxLayoutContainer();
        h.add(refreshButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        h.add(clearButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        h.add(newPatButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));

        final VerticalLayoutContainer v = new VerticalLayoutContainer();
        v.add(h, new VerticalLayoutContainer.VerticalLayoutData(1, -1, DEFAULT_MARGINS));
        v.add(TEXT_AREA, new VerticalLayoutContainer.VerticalLayoutData(1, 1, DEFAULT_MARGINS));

        final ContentPanel cp = new ContentPanel();
        cp.setHeadingText("Resource Server Monitor");
        cp.add(v);
        return cp;
    }

    private static void refresh() {
        getService().getMessageList(new AsyncCallback<List<Msg>>() {
            @Override
            public void onFailure(Throwable caught) {
                handleException(caught);
            }

            @Override
            public void onSuccess(List<Msg> result) {
                final SafeHtmlBuilder sb = new SafeHtmlBuilder();
                if (result != null && !result.isEmpty()) {
                    for (Msg msg : result) {
//                        final String formattedDate = DateTimeFormat.getLongDateFormat().format(msg.getDate());
//                        sb.appendEscaped(formattedDate).appendEscaped(" - ")
//                                .appendHtmlConstant(msg.getMessage())
//                                .append(SafeHtmlUtils.fromSafeConstant("\n"));
                        sb.appendHtmlConstant(msg.getFormattedMessage());
//                                .append(SafeHtmlUtils.fromSafeConstant("\n"));

                    }
                }
                TEXT_AREA.setValue(sb.toSafeHtml().asString());
            }
        });
    }

    private static void handleException(Throwable caught) {
        // handle exception here
    }

    public static ServiceAsync getService() {
        return SERVICE;
    }
}
