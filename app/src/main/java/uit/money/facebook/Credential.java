package uit.money.facebook;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.Collections;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;
import model.model.User;

import static uit.money.app.Constants.AUTH_URL;
import static uit.money.app.Constants.BASE_URL;

public class Credential {
    private static final String TAG = "Credential";
    private static AccessTokenTracker accessTokenTracker = null;
    private static AccessToken accessToken = null;
    private static final Boolean SYNC = false;

    public static void initializeLogin(final Credential.Callback nextActivity) {
        checkAndLogin(nextActivity);

        // Don't call this method more than one time
        if (accessTokenTracker != null) return;
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken accessToken) {
                checkAndLogin(nextActivity);
            }
        };
    }

    private static void checkAndLogin(final Callback nextActivity) {
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) return;

        Callback callback = () -> {
            setCurrentUser();
            nextActivity.call();
        };

        if (SYNC) {
            SyncCredentials credentials = SyncCredentials.nickname(accessToken.getUserId(), false);
            SyncUser.logInAsync(credentials, AUTH_URL, new SyncUser.Callback<SyncUser>() {
                @Override
                public void onSuccess(@NonNull SyncUser user) {
                    setSyncConfiguration();
                    callback.call();
                }

                @Override
                public void onError(@NonNull ObjectServerError error) {
                    setLocalConfiguration();
                    callback.call();
                }
            });
        } else {
            setLocalConfiguration();
            callback.call();
        }
    }

    private static void setCurrentUser() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            User user = new User();
            user.setFbid(accessToken.getUserId());
            user.setName(Profile.getCurrentProfile().getName());
            User.setCurrentUser(realm.copyToRealmOrUpdate(user));
        });
    }

    private static void setSyncConfiguration() {
        SyncConfiguration configuration = new SyncConfiguration
                .Builder(SyncUser.current(), BASE_URL + "/~/" + accessToken.getUserId())
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    private static void setLocalConfiguration() {
        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .name("realm.realm")
                .build();

        Realm.setDefaultConfiguration(configuration);
    }

    public static void login(AppCompatActivity activity) {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(activity, Collections.singletonList("public_profile"));
    }

    public static void logout() {
        LoginManager.getInstance().logOut();
    }

    public interface Callback {
        void call();
    }
}
