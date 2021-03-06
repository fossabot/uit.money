package voice.recognizer.open;

import java.util.ArrayList;

import voice.utils.InterfaceWalletActivity;
import voice.utils.InterfaceRecognizer;

import static voice.utils.Utils.matchEachInput;

public class OpenCreateBill implements InterfaceRecognizer {
    private static final String REGEX = "tạo giao dịch mới";

    @Override
    public boolean run(ArrayList<String> inputs, InterfaceWalletActivity activity) {
        if (!matchEachInput(inputs, REGEX)) return false;

        activity.openCreateBill(null);
        return true;
    }

}
