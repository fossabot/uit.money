package model.model;import org.parceler.Parcel;import io.realm.Realm;import io.realm.RealmObject;import io.realm.RealmResults;import io.realm.annotations.LinkingObjects;import io.realm.annotations.PrimaryKey;import io.realm.annotations.Required;import io.realm.model_model_UserRealmProxy;/** * <Fields> * * @see User#fbid                   {@link String} * @see User#name                   {@link String} */@Parcel(implementations = {model_model_UserRealmProxy.class},        value = Parcel.Serialization.BEAN,        analyze = {User.class})public class User extends RealmObject {    private static User currentUser;    @LinkingObjects("user")    private final RealmResults<Wallet> wallets = null;    @PrimaryKey    private String fbid;    @Required    private String name;    public User() {    }    public User(Callback callback) {        callback.call(this);    }    public static User getCurrentUser() {        return currentUser;    }    public static void setCurrentUser(User currentUser) {        User.currentUser = currentUser;    }    public String getFbid() {        return fbid;    }    public void setFbid(String fbid) {        this.fbid = fbid;    }    public String getName() {        return name;    }    public void setName(String name) {        this.name = name;    }    public RealmResults<Wallet> getWallets() {        return Realm.getDefaultInstance()                .where(Wallet.class)                .equalTo("user.fbid", fbid)                .findAllAsync();    }    public interface Callback {        void call(User user);    }}