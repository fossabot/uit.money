package model.model.transaction;

import android.databinding.ObservableInt;

import org.parceler.Parcel;

import java.util.Date;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import io.realm.model_model_transaction_BillRealmProxy;
import model.model.Person;
import model.model.R;
import model.model.Wallet;
import model.model.util.Organization;

import static model.Const.BILL;
import static model.Const.BUY;
import static model.Const.getResource;
import static model.Const.getString;

/**
 * Bill tức là hóa đơn
 * ở đây, Bill đại diện cho một danh sách các BillDetail
 * ví dụ: đây là hóa đơn đi mua sắm gồm có
 * + 2 chai nước
 * + 3 cái ly
 * <Fields>
 *
 * @see Bill#id                {@link Integer}
 * @see Bill#wallet            {@link Wallet}              >>  {@link Wallet#bills}
 * @see Bill#time              {@link Date}
 * @see Bill#location          {@link String}
 * @see Bill#with              {@link Person}              >>  {@link Person#multilineDealsWith}
 * @see Bill#organization      {@link Organization}        >>  {@link Organization#bills}
 * @see Bill#buyOrSell         {@link Boolean}
 */
@Parcel(implementations = {model_model_transaction_BillRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Bill.class})
public class Bill extends RealmObject implements Transaction, TransactionModel {
    @Ignore
    private RealmResults<BillDetail> billDetails = null;
    @Ignore
    public long money = 0;
    @Ignore
    public final ObservableInt maxCount = new ObservableInt(0);
    @PrimaryKey
    private int id;
    private Wallet wallet;
    @Required
    private Date time;
    private String location;
    private Person with;
    private Organization organization;
    private boolean buyOrSell;

    public Bill() {
        this.time = new Date();
    }

    public Bill(Wallet wallet) {
        this.time = new Date();
        this.wallet = wallet;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public void initialize() {
        getBillDetails();
        billDetails.removeAllChangeListeners();
        billDetails.addChangeListener(this::updateBindingValue);
        updateBindingValue(billDetails);
    }

    public RealmResults<BillDetail> getBillDetails() {
        if (billDetails == null) {
            billDetails = Realm.getDefaultInstance()
                    .where(BillDetail.class)
                    .equalTo("bill.id", id)
                    .findAllAsync();
        }
        return billDetails;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Person getWith() {
        return with;
    }

    public void setWith(Person with) {
        this.with = with;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public boolean isBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(boolean buyOrSell) {
        this.buyOrSell = buyOrSell;
    }

    private void updateBindingValue(RealmResults<BillDetail> billDetails) {
        long money = 0;
        int maxAmount = 0;
        for (BillDetail billDetail : billDetails) {
            money += billDetail.getMoney();
            final int amount = billDetail.getAmount();
            if (amount > maxAmount) maxAmount = amount;
        }
        this.maxCount.set(maxAmount);
        setMoney(money);
    }

    public int getMoneyColor() {
        return getResource().getColor(
                buyOrSell == BUY ? R.color.buyColor : R.color.sellColor,
                null
        );
    }

    @Override
    public String getAction() {
        return getString(
                buyOrSell == BUY ? R.string.buy : R.string.sell
        );
    }

    @Override
    public long getMoney() {
        return money * (buyOrSell == BUY ? -1 : 1);
    }

    @Override
    public int getType() {
        return BILL;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void autoId() {
        RealmResults<Bill> bills = Realm.getDefaultInstance()
                .where(Bill.class)
                .findAllAsync();
        bills.load();
        id = bills.size() == 0
                ? 0
                : Objects.requireNonNull(bills.max("id")).intValue() + 1;
    }
}