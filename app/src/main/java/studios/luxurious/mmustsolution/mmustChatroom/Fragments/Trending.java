package studios.luxurious.mmustsolution.mmustChatroom.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.mmustChatroom.Constants;
import studios.luxurious.mmustsolution.mmustChatroom.Helper.BlogViewHolder;
import studios.luxurious.mmustsolution.mmustChatroom.Modal.Blog;


public class Trending extends Fragment {

    private RecyclerView blog_list;
    private FirestorePagingAdapter<Blog, BlogViewHolder> paging_adapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initializing the Recycler View
        blog_list = view.findViewById(R.id.blog_list);
        populateBlogList();
        blog_list.setAdapter(paging_adapter);
        blog_list.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        blog_list.setLayoutManager(linearLayoutManager);
        coordinatorLayout = view.findViewById(R.id.main_layout);
        paging_adapter.startListening();

        return view;
    }


    // Populating Blog List
    private void populateBlogList() {

        Query query = FirebaseFirestore.getInstance()
                .collection("Posts")
                .limit(10)
                .orderBy("Likes", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(Constants.PAGING_SIZE_PER_LOAD)
                .build();

        FirestorePagingOptions<Blog> options = new FirestorePagingOptions.Builder<Blog>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Blog.class)
                .build();


        paging_adapter = new FirestorePagingAdapter<Blog, BlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull Blog model) {

                holder.setImage(model.getImage(), getActivity());
                holder.setUser(getActivity(), model.getUser(), getActivity());
                holder.setDate(model.getTime());
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());
                holder.setLikes(getActivity(), model.getID());
                holder.setComments(model.getID());
                holder.showPostDetails(model.getImage(), model.getUser(), model.getTime(), model.getTitle(), model.getDetails(),  getActivity(),model.getID());

            }

            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.blog_row, group, false);
                return new BlogViewHolder(view, getActivity());
            }
        };


    }
}
