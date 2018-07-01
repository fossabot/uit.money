package uit.money.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import io.realm.Realm;
import io.realm.RealmResults;
import model.model.transaction.Bill;
import model.model.transaction.BillDetail;
import ui.Row;
import ui.Text;
import uit.money.R;

import static model.Utils.getMoney;
import static ui.utils.Const.BOLD;
import static ui.utils.Const.REGULAR;

public class BillDetailsView extends LinearLayoutCompat {
    private static final int TEXT_SIZE = 15;
    private static final int TEXT_COLOR_ID = R.color._item_color;
    private final int TEXT_COLOR;
    private int billId;

    public BillDetailsView(Context context) {
        super(context);
        TEXT_COLOR = context.getColor(TEXT_COLOR_ID);
        initialize(context, null);
    }

    private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayoutCompat.VERTICAL);
        initializeAttrs(context, attrs);
        initializeItems();
    }

    private void initializeAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray;

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.BillDetailsView);
        billId = typedArray.getInt(R.styleable.BillDetailsView__billId, -1);

        typedArray.recycle();
    }

    private void initializeItems() {
        final Realm realm = Realm.getDefaultInstance();
        Bill bill = realm
                .where(Bill.class)
                .equalTo("id", billId)
                .findFirst();
        if (bill == null) {
            removeAllViews();
            return;
        }

        final RealmResults<BillDetail> billDetails = bill.getBillDetails();
        billDetails.addChangeListener(this::addChild);
        addChild(billDetails);
    }

    @SuppressLint("ResourceType")
    private void addChild(RealmResults<BillDetail> details) {
        removeAllViews();
        final Context context = getContext();
        for (BillDetail detail : details) {
            final Row row = new Row(context);

            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.bill_detail_height)));

            row.addView(getObjectView(context, detail));
            row.addView(getQuantityView(context, detail));
            row.addView(getCrossView(context));
            row.addView(getMoneyView(context, detail));

            addView(row);
        }
    }

    @NonNull
    private Text getObjectView(Context context, BillDetail detail) {
        final Text object = new Text(context);
        object.setGravity(Gravity.CENTER_VERTICAL);
        object.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT,
                1
        ));
        object.setText(detail.getObject().getName());
        object.setTextColor(TEXT_COLOR);
        object.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        object.set_font(REGULAR);
        return object;
    }

    @NonNull
    private Text getQuantityView(Context context, BillDetail detail) {
        final Text quantity = new Text(context);
        quantity.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        quantity.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        quantity.setText(String.valueOf(detail.getQuantity()));
        quantity.setTextColor(TEXT_COLOR);
        quantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        quantity.set_font(BOLD);
        return quantity;
    }

    @NonNull
    private Text getCrossView(Context context) {
        final Text cross = new Text(context);
        final int padding = (int) getResources().getDimension(R.dimen.bill_detail_cross_padding);
        cross.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        cross.setGravity(Gravity.CENTER_VERTICAL);
        cross.setPadding(padding, 0, padding, 0);
        cross.setText("✕");
        cross.setTextColor(TEXT_COLOR);
        cross.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        cross.set_font(BOLD);
        return cross;
    }

    @NonNull
    private Text getMoneyView(Context context, BillDetail detail) {
        final Text money = new Text(context);
        money.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        money.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.bill_detail_money_min_width));
        money.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        money.setText(getMoney(detail.getUnitPrice()));
        money.setTextColor(TEXT_COLOR);
        money.set_font(BOLD);
        money.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        return money;
    }

    public BillDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TEXT_COLOR = context.getColor(TEXT_COLOR_ID);
        initialize(context, attrs);
    }

    public BillDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TEXT_COLOR = context.getColor(TEXT_COLOR_ID);
        initialize(context, attrs);
    }

    public void set_billId(int billId) {
        this.billId = billId;
        initializeItems();
    }
}
