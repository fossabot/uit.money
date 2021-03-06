package uit.money.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.realm.RealmRecyclerViewAdapter;
import model.model.transaction.Bill;
import model.model.transaction.BillDetail;
import uit.money.R;
import uit.money.databinding.ItemBillDetailBinding;

public class BillDetailRecyclerViewAdapter extends RealmRecyclerViewAdapter<BillDetail, BillDetailRecyclerViewAdapter.ViewHolder> {
    public BillDetailRecyclerViewAdapter(Bill bill) {
        super(bill.getBillDetails(), true);
    }

    public static BillDetailRecyclerViewAdapter getInstance(Bill bill) {
        return new BillDetailRecyclerViewAdapter(bill);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemBillDetailBinding viewDataBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_bill_detail,
                parent, false
        );
        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBillDetailBinding binding;

        ViewHolder(ItemBillDetailBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            this.binding = binding;
        }

        void bind(BillDetail billDetail) {
            binding.setBillDetail(billDetail);
            binding.executePendingBindings();
        }
    }
}