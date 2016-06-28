package ua.com.tlftgames.waymc.screen.ui.window;

import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.listener.Listener;
import ua.com.tlftgames.waymc.screen.ui.ReceiptGroup;
import ua.com.tlftgames.waymc.screen.ui.ScrollPane;

public class WorkshopWindowBody extends WindowBody implements Listener {
    private IndustrialWindowManager manager;
    private ReceiptGroup receiptsBlock;

    public WorkshopWindowBody(IndustrialWindowManager manager) {
        super();
        this.manager = manager;

        receiptsBlock = new ReceiptGroup(this.manager.getHelper(), this.manager.getWindow().getInfo(), true);

        ScrollPane receiptPane = this.manager.getHelper().createScrollPane(receiptsBlock);
        receiptPane.setBounds(0, 10, this.getWidth(), this.getHeight() - 10);
        this.addActor(receiptPane);

        int[] events = { Dispatcher.EVENT_ITEMS_CHANGED, Dispatcher.EVENT_RECEIPTS_CHANGED };
        Dispatcher.getInstance().addListener(events, this);
    }

    @Override
    public void fireEvent(int event) {
        this.receiptsBlock.update();
    }
}
