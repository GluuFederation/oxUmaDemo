package org.xdi.uma.demo.rp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import org.xdi.gwt.client.GwtUtils;
import org.xdi.uma.demo.rp.shared.Conf;
import org.xdi.uma.demo.common.gwt.Msg;
import org.xdi.uma.demo.common.gwt.Phones;
import org.xdi.uma.demo.common.gwt.ui.ProgressDialog;
import org.xdi.uma.demo.rp.client.events.LoginEvent;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 15/05/2013
 */

public class RP implements EntryPoint {

    public static final Margins DEFAULT_MARGINS = new Margins(2, 2, 2, 2);

    private static final ServiceAsync SERVICE = GWT.create(Service.class);
    private static final TextArea TEXT_AREA = new TextArea();
    private static final EventBus EVENT_BUS = new SimpleEventBus();
    private static Conf CONF;

    private final ListStore<String> store = new ListStore<String>(new ModelKeyProvider<String>() {
        @Override
        public String getKey(String item) {
            return item;
        }
    });

    private final ListViewSelectionModel<String> m_sm = new ListViewSelectionModel<String>();

    @Override
    public void onModuleLoad() {
        initEventBus();

        SERVICE.getConf(new AsyncCallback<Conf>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtUtils.showError("Failed to load RP configuration.");
            }

            @Override
            public void onSuccess(Conf conf) {
                CONF = conf;
                init();
            }
        });
    }

    private void init() {
        if (LoginController.hasAccessToken()) {
            showUI();
        } else {
//            LoginController.userLogin();
            LoginController.clientAuthentication();
        }
    }

    private void initEventBus() {
        getEventBus().addHandler(LoginEvent.TYPE, new LoginEvent.Handler() {
            @Override
            public void update(LoginEvent event) {
                if (event.isLogin()) {
                    showUI();
                }
            }
        });
    }

    public static EventBus getEventBus() {
        return EVENT_BUS;
    }

    public void showUI() {
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

    public static ServiceAsync getService() {
        return SERVICE;
    }

    private ContentPanel createContent() {
        final BorderLayoutContainer.BorderLayoutData westData = new BorderLayoutContainer.BorderLayoutData(0.3);
        westData.setCollapsible(true);
        westData.setCollapseMini(true);
        westData.setSplit(true);
        westData.setFloatable(true);

        final BorderLayoutContainer.BorderLayoutData centerData = new BorderLayoutContainer.BorderLayoutData(0.7);
        centerData.setCollapsible(true);
        centerData.setCollapseMini(true);
        centerData.setSplit(true);
        centerData.setMargins(DEFAULT_MARGINS);

        final BorderLayoutContainer b = new BorderLayoutContainer();
        b.setWestWidget(createWestWidget(), westData);
        b.setCenterWidget(createCenterWidget(), centerData);

        final ContentPanel cp = new ContentPanel();
        cp.setHeadingText("Requesting Party Monitor");
        cp.add(b);
        return cp;
    }

    private IsWidget createWestWidget() {
        final ListView<String, String> list = new ListView<String, String>(store, new IdentityValueProvider<String>());
        list.setSelectionModel(m_sm);

        final TextButton viewButton = new TextButton("View");
        final TextButton addButton = new TextButton("Add");
        final TextButton removeButton = new TextButton("Remove");
        final TextButton editButton = new TextButton("Edit");
        final TextButton demoButton = new TextButton("Demo");
        final TextButton clearStateButton = new TextButton("Clear state (new demo)");

        final String demoTooltip = "1. Call RS without RPT<br/>" +
                "2. Call RS with RPT which is not authorized<br/>" +
                "3. Authorize RPT (by ticket provided by RS).<br/>" +
                "4. Call RS with authorized RPT";
        final String clearStateTooltip = "1. Clear phone list<br/>" +
                "2. Clear logs<br/>" +
                "3. Renew AAT<br/>" +
                "4. Renew RPT";

        final ToolTipConfig demoToolTipConfig = new ToolTipConfig();
        demoToolTipConfig.setBodyHtml(demoTooltip);
        demoToolTipConfig.setDismissDelay(0);

        final ToolTipConfig clearStateTooltipConfig = new ToolTipConfig();
        clearStateTooltipConfig.setBodyHtml(clearStateTooltip);
        clearStateTooltipConfig.setDismissDelay(0);

        demoButton.setToolTipConfig(demoToolTipConfig);
        viewButton.setToolTip("View phones");
        addButton.setToolTip("Add phone");
        removeButton.setToolTip("Remove phone");
        editButton.setToolTip("Edit phone");
        clearStateButton.setToolTipConfig(clearStateTooltipConfig);

        viewButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final ProgressDialog d = new ProgressDialog("Request phones...");
                d.show();
                getService().getPhoneList(new AsyncCallback<Phones>() {
                    @Override
                    public void onFailure(Throwable p_throwable) {
                        d.hide();
                        handleException(p_throwable);
                    }

                    @Override
                    public void onSuccess(Phones p_phones) {
                        try {
                            refresh();
                            final List<String> phones = p_phones.getPhones();
                            store.clear();
                            if (phones != null && !phones.isEmpty()) {
                                store.addAll(phones);
                            }
                            store.commitChanges();
                        } finally {
                            d.hide();
                        }
                    }
                });
            }
        });

        addButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                final NewPhoneDialog d = new NewPhoneDialog();
                d.show();
                d.getButtonById(Dialog.PredefinedButton.OK.name()).addSelectHandler(new SelectEvent.SelectHandler() {
                    @Override
                    public void onSelect(SelectEvent event) {
                        final String newPhone = d.getTextField().getValue();
                        if (!GwtUtils.isEmpty(newPhone)) {
                            getService().addPhone(newPhone, new AsyncCallback<Boolean>() {
                                @Override
                                public void onFailure(Throwable p_throwable) {
                                    handleException(p_throwable);
                                }

                                @Override
                                public void onSuccess(Boolean p_boolean) {
                                    refresh();
                                    if (p_boolean != null && p_boolean) {
                                        store.add(newPhone);
                                        store.commitChanges();
                                    }
                                }
                            });
                        } else {
                            GwtUtils.showInformation("Unable to add empty phone.");
                        }
                    }
                });
            }
        });

        demoButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final ProgressDialog d = new ProgressDialog("Demo...");
                d.show();
                getService().demo(new AsyncCallback<Phones>() {
                    @Override
                    public void onFailure(Throwable p_throwable) {
                        d.hide();
                        handleException(p_throwable);
                    }

                    @Override
                    public void onSuccess(Phones phones) {
                        try {
                            refresh();
                            store.clear();

                            if (phones != null && phones.getPhones() != null && !phones.getPhones().isEmpty()) {
                                store.addAll(phones.getPhones());
                            }
                            store.commitChanges();
                        } finally {
                            d.hide();
                        }
                    }
                });
            }
        });

        clearStateButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final ProgressDialog d = new ProgressDialog("Clear state...");
                d.show();
                getService().clearState(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable p_throwable) {
                        d.hide();
                        handleException(p_throwable);
                        GwtUtils.showError("Unable to clear logs.");
                    }

                    @Override
                    public void onSuccess(Void p_void) {
                        d.hide();
                        store.clear();
                        store.commitChanges();
                        TEXT_AREA.setValue("");
                        refresh();
                    }
                });
            }
        });

        removeButton.setEnabled(false);
        removeButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final String phone = m_sm.getSelectedItem();
                if (GwtUtils.isEmpty(phone)) {
                    GwtUtils.showInformation("Please select phone to remove");
                    return;
                }

                getService().removePhone(phone, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable p_throwable) {
                        handleException(p_throwable);
                    }

                    @Override
                    public void onSuccess(Boolean p_result) {
                        refresh();
                        if (p_result != null && p_result) {
                            store.remove(phone);
                            store.commitChanges();
                        } else {
                            GwtUtils.showInformation("Failed to remove phone.");
                        }
                    }
                });

            }
        });
        m_sm.addSelectionHandler(new SelectionHandler<String>() {
            @Override
            public void onSelection(SelectionEvent<String> p_stringSelectionEvent) {
                final List<String> items = m_sm.getSelectedItems();
                removeButton.setEnabled(items != null && items.size() == 1);
            }
        });

        final HBoxLayoutContainer toolbar = new HBoxLayoutContainer();
        toolbar.add(viewButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        toolbar.add(addButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        toolbar.add(removeButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
//        toolbar.add(editButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        toolbar.add(new Label("  "), new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        toolbar.add(demoButton, new BoxLayoutContainer.BoxLayoutData(new Margins(2, 2, 2, 10)));
        toolbar.add(clearStateButton, new BoxLayoutContainer.BoxLayoutData(new Margins(2, 2, 2, 10)));

        final VerticalLayoutContainer v = new VerticalLayoutContainer();
        v.add(toolbar, new VerticalLayoutContainer.VerticalLayoutData(-1, -1, DEFAULT_MARGINS));
        v.add(new Label("Phone number(s):"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, DEFAULT_MARGINS));
        v.add(new Label("(hosted by Resource Server on " + CONF.getRsHost()), new VerticalLayoutContainer.VerticalLayoutData(1, -1, DEFAULT_MARGINS));
        v.add(list, new VerticalLayoutContainer.VerticalLayoutData(250, 450, DEFAULT_MARGINS));

        final ContentPanel container = new ContentPanel();
        container.setWidget(v);
        return container;
    }

    private VerticalLayoutContainer createCenterWidget() {
        TEXT_AREA.setReadOnly(true);

        final TextButton refreshButton = new TextButton("Refresh");
        refreshButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                refresh();
            }
        });

        final TextButton newAatButton = new TextButton("Obtain new AAT via client authentication");
        newAatButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                LoginController.logout();
                LoginController.clientAuthentication();

//                final ProgressDialog progressDialog = new ProgressDialog("Perform operation...");
//                progressDialog.show();
//                getService().obtainNewAatViaClientAuthentication(new AsyncCallback<String>() {
//                    @Override
//                    public void onFailure(Throwable caught) {
//                        progressDialog.hide();
//                        handleException(caught);
//                        GwtUtils.showError("Unable to obtain new AAT token.");
//                        refresh();
//                    }
//
//                    @Override
//                    public void onSuccess(String result) {
//                        progressDialog.hide();
//                        GwtUtils.showInformation("AAT token is obtained successfully.");
//                        refresh();
//                    }
//                });
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

        final TextButton newRptButton = new TextButton("Obtain new RPT token");
        newRptButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final ProgressDialog progressDialog = new ProgressDialog("Perform operation...");
                progressDialog.show();
                getService().obtainNewRpt(new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        progressDialog.hide();
                        handleException(caught);
                        GwtUtils.showError("Unable to obtain new RPT token.");
                        refresh();
                    }

                    @Override
                    public void onSuccess(String result) {
                        progressDialog.hide();
                        GwtUtils.showInformation("RPT token is obtained successfully.");
                        refresh();
                    }
                });
            }
        });

        final TextButton logoutButton = new TextButton("Logout");
        logoutButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                LoginController.logout();
                LoginController.userLogin();
            }
        });


        final BoxLayoutContainer.BoxLayoutData spaceData = new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS);
        spaceData.setFlex(1);

        final HBoxLayoutContainer h = new HBoxLayoutContainer();
        h.add(refreshButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        h.add(clearButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        h.add(newAatButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        h.add(newRptButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));
        h.add(new Label(), spaceData);
        h.add(logoutButton, new BoxLayoutContainer.BoxLayoutData(DEFAULT_MARGINS));

        final VerticalLayoutContainer v = new VerticalLayoutContainer();
        v.add(h, new VerticalLayoutContainer.VerticalLayoutData(1, -1, DEFAULT_MARGINS));
        v.add(TEXT_AREA, new VerticalLayoutContainer.VerticalLayoutData(1, 1, DEFAULT_MARGINS));
        return v;
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
                        sb.appendHtmlConstant(msg.getFormattedMessage());
                    }
                }
                TEXT_AREA.setValue(sb.toSafeHtml().asString());
            }
        });
    }

    private static void handleException(Throwable caught) {
        caught.printStackTrace();
    }
}
