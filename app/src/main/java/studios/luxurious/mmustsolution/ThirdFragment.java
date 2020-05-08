package studios.luxurious.mmustsolution;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ThirdFragment extends Fragment {

    private DatabaseReference databaseid2, databasesc2, databasebook2, databaseele2, databasedoc2, databaseothers2;
    private RecyclerView idrecyclerview, screcyclerview, bookrecyclerview, electrecyclerview, docrecyclerview, otherrecyclerview;
    private CardView itemsavailable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.page32, container, false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        databaseid2 = FirebaseDatabase.getInstance().getReference().child("Found_IDs");
        databasesc2 = FirebaseDatabase.getInstance().getReference().child("Found_School_IDs");
        databasebook2 = FirebaseDatabase.getInstance().getReference().child("Found_Books");
        databaseele2 = FirebaseDatabase.getInstance().getReference().child("Found_Electronics");
        databasedoc2 = FirebaseDatabase.getInstance().getReference().child("Found_Documents");
        databaseothers2 = FirebaseDatabase.getInstance().getReference().child("Found_Others");


        databaseid2.keepSynced(true);
        databasesc2.keepSynced(true);
        databasebook2.keepSynced(true);
        databaseele2.keepSynced(true);
        databasedoc2.keepSynced(true);
        databaseothers2.keepSynced(true);


        idrecyclerview = view.findViewById(R.id.rv);
        idrecyclerview.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        screcyclerview = view.findViewById(R.id.rv2);
        screcyclerview.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        bookrecyclerview = view.findViewById(R.id.rv3);
        bookrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        electrecyclerview = view.findViewById(R.id.rv4);
        electrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        docrecyclerview = view.findViewById(R.id.rv5);
        docrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        otherrecyclerview = view.findViewById(R.id.rv6);
        otherrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        idrecyclerview.setNestedScrollingEnabled(false);
        screcyclerview.setNestedScrollingEnabled(false);
        bookrecyclerview.setNestedScrollingEnabled(false);
        electrecyclerview.setNestedScrollingEnabled(false);
        docrecyclerview.setNestedScrollingEnabled(false);
        otherrecyclerview.setNestedScrollingEnabled(false);

        if (!IsConnected()){

            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            Toast.makeText(getActivity(), "No Internet connection", Toast.LENGTH_SHORT).show();

        }

        if (isAdded()) {

            itemsavailable = view.findViewById(R.id.itemsavailable);


                    Handler ls = new Handler();
                    ls.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            itemsavailable.setVisibility(View.VISIBLE);

                            Handler s = new Handler();
                            s.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    itemsavailable.setVisibility(View.GONE);
                                }
                            },3500);

                        }
                    },2500);


            FirebaseRecyclerAdapter<Found, FoundViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Found, FoundViewHolder>(

                    Found.class,
                    R.layout.foundid,
                    FoundViewHolder.class,
                    databaseid2
            ) {
                @Override
                protected void populateViewHolder(final FoundViewHolder viewHolder, Found model, int position) {

                    String foundkey = getRef(position).getKey();
                    viewHolder.setIdname(model.getIdname());
                    viewHolder.setIdno(model.getIdno());
                    viewHolder.setIdtel(model.getIduid());
                    viewHolder.setIdtime(model.getIdtime());
                    viewHolder.setExpireTime(model.getExpiretime());

                    TextView textView = viewHolder.view.findViewById(R.id.expiretime);
                    String posttime = textView.getText().toString();
                    if (!TextUtils.isEmpty(posttime)) {
                        long r = Long.parseLong(posttime);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long ti = timestamp.getTime();

                        long diff = ti - r;

                        if (diff >= 1000 * 60 *60*24*14) {
                            databaseid2.child(foundkey).removeValue();
                        }
                    }

                    viewHolder.cardviewid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            TextView uid = viewHolder.view.findViewById(R.id.telid);
                            TextView date = viewHolder.view.findViewById(R.id.timeid);

                            String uidd = uid.getText().toString();
                            String datee = date.getText().toString();

                            Intent r = new Intent(getActivity(), Seer.class);
                            r.putExtra("Uid", uidd);
                            r.putExtra("Date", datee);
                            startActivity(r);

                        }
                    });


                }
            };

            idrecyclerview.setAdapter(firebaseRecyclerAdapter);


            FirebaseRecyclerAdapter<Found, FoundViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Found, FoundViewHolder>(

                    Found.class,
                    R.layout.foundschool,
                    FoundViewHolder.class,
                    databasesc2
            ) {
                @Override
                protected void populateViewHolder(final FoundViewHolder viewHolder, Found model, int position) {

                    viewHolder.setScname(model.getScname());
                    viewHolder.setregno(model.getRegno());
                    viewHolder.setsctime(model.getSctime());
                    viewHolder.settelsc(model.getScuid());

                    String foundkey = getRef(position).getKey();
                    viewHolder.setExpireTime(model.getExpiretime());

                    TextView textView = viewHolder.view.findViewById(R.id.expiretime);
                    String posttime = textView.getText().toString();
                    if (!TextUtils.isEmpty(posttime)) {
                        long r = Long.parseLong(posttime);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long ti = timestamp.getTime();

                        long diff = ti - r;

                        if (diff >= 1000 * 60 *60*24*14) {
                            databasesc2.child(foundkey).removeValue();
                        }
                    }

                    viewHolder.cardviewsc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            TextView uid = viewHolder.view.findViewById(R.id.schid);
                            TextView date = viewHolder.view.findViewById(R.id.timesc);

                            String uidd = uid.getText().toString();
                            String datee = date.getText().toString();

                            Intent r = new Intent(getActivity(), Seer.class);
                            r.putExtra("Uid", uidd);
                            r.putExtra("Date", datee);
                            startActivity(r);

                        }
                    });


                }
            };

            screcyclerview.setAdapter(firebaseRecyclerAdapter2);

            FirebaseRecyclerAdapter<Found, FoundViewHolder> firebaseRecyclerAdapter3 = new FirebaseRecyclerAdapter<Found, FoundViewHolder>(

                    Found.class,
                    R.layout.foundbook,
                    FoundViewHolder.class,
                    databasebook2
            ) {
                @Override
                protected void populateViewHolder(final FoundViewHolder viewHolder, Found model, int position) {

                    viewHolder.setBookTitle(model.getBooktitle());
                    viewHolder.setBooksubject(model.getSubject());
                    viewHolder.setbooktime(model.getBooktime());
                    viewHolder.setbookTel(model.getBookuid());
                    String foundkey = getRef(position).getKey();
                    viewHolder.setExpireTime(model.getExpiretime());

                    TextView textView = viewHolder.view.findViewById(R.id.expiretime);
                    String posttime = textView.getText().toString();
                    if (!TextUtils.isEmpty(posttime)) {
                        long r = Long.parseLong(posttime);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long ti = timestamp.getTime();

                        long diff = ti - r;

                        if (diff >= 1000 * 60 *60*24*14) {
                            databasebook2.child(foundkey).removeValue();
                        }
                    }

                    viewHolder.cardviewbook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            TextView uid = viewHolder.view.findViewById(R.id.bookuidid);
                            TextView date = viewHolder.view.findViewById(R.id.timebook);

                            String uidd = uid.getText().toString();
                            String datee = date.getText().toString();

                            Intent r = new Intent(getActivity(), Seer.class);
                            r.putExtra("Uid", uidd);
                            r.putExtra("Date", datee);
                            startActivity(r);

                        }
                    });


                }
            };

            bookrecyclerview.setAdapter(firebaseRecyclerAdapter3);


            FirebaseRecyclerAdapter<Found, FoundViewHolder> firebaseRecyclerAdapter4 = new FirebaseRecyclerAdapter<Found, FoundViewHolder>(

                    Found.class,
                    R.layout.founddocuments,
                    FoundViewHolder.class,
                    databasedoc2
            ) {
                @Override
                protected void populateViewHolder(final FoundViewHolder viewHolder, Found model, int position) {

                    viewHolder.setdoctype(model.getDoctype());
                    viewHolder.setdocname(model.getDocname());
                    viewHolder.setdoctel(model.getDocuid());
                    viewHolder.setdoctime(model.getDoctime());
                    String foundkey = getRef(position).getKey();
                    viewHolder.setExpireTime(model.getExpiretime());

                    TextView textView = viewHolder.view.findViewById(R.id.expiretime);
                    String posttime = textView.getText().toString();
                    if (!TextUtils.isEmpty(posttime)) {
                        long r = Long.parseLong(posttime);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long ti = timestamp.getTime();

                        long diff = ti - r;

                        if (diff >= 1000 * 60 *60*24*14) {
                            databasedoc2.child(foundkey).removeValue();
                        }
                    }


                    viewHolder.cardviewdoc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            TextView uid = viewHolder.view.findViewById(R.id.tedocuid);
                            TextView date = viewHolder.view.findViewById(R.id.timedoc);

                            String uidd = uid.getText().toString();
                            String datee = date.getText().toString();

                            Intent r = new Intent(getActivity(), Seer.class);
                            r.putExtra("Uid", uidd);
                            r.putExtra("Date", datee);
                            startActivity(r);

                        }
                    });


                }
            };

            docrecyclerview.setAdapter(firebaseRecyclerAdapter4);


            FirebaseRecyclerAdapter<Found, FoundViewHolder> firebaseRecyclerAdapter5 = new FirebaseRecyclerAdapter<Found, FoundViewHolder>(

                    Found.class,
                    R.layout.foundelect,
                    FoundViewHolder.class,
                    databaseele2
            ) {
                @Override
                protected void populateViewHolder(final FoundViewHolder viewHolder, Found model, int position) {

                    viewHolder.seteletype(model.getElecttype());
                    viewHolder.setelemodel(model.getModel());
                    viewHolder.seteletime(model.getElectime());
                    viewHolder.seteletel(model.getElectuid());

                    String foundkey = getRef(position).getKey();
                    viewHolder.setExpireTime(model.getExpiretime());

                    TextView textView = viewHolder.view.findViewById(R.id.expiretime);
                    String posttime = textView.getText().toString();
                    if (!TextUtils.isEmpty(posttime)) {
                        long r = Long.parseLong(posttime);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long ti = timestamp.getTime();

                        long diff = ti - r;

                        if (diff >= 1000 * 60 *60*24*14) {
                            databaseele2.child(foundkey).removeValue();
                        }
                    }
                    viewHolder.cardviewelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            TextView uid = viewHolder.view.findViewById(R.id.electuid);
                            TextView date = viewHolder.view.findViewById(R.id.timeele);

                            String uidd = uid.getText().toString();
                            String datee = date.getText().toString();

                            Intent r = new Intent(getActivity(), Seer.class);
                            r.putExtra("Uid", uidd);
                            r.putExtra("Date", datee);
                            startActivity(r);

                        }
                    });


                }
            };

            electrecyclerview.setAdapter(firebaseRecyclerAdapter5);


            FirebaseRecyclerAdapter<Found, FoundViewHolder> firebaseRecyclerAdapter6 = new FirebaseRecyclerAdapter<Found, FoundViewHolder>(

                    Found.class,
                    R.layout.foundothers,
                    FoundViewHolder.class,
                    databaseothers2
            ) {
                @Override
                protected void populateViewHolder(final FoundViewHolder viewHolder, Found model, int position) {

                    viewHolder.setitem(model.getOthername());
                    viewHolder.setitemdesc(model.getOtherno());
                    viewHolder.setitemtel(model.getOtheruid());
                    viewHolder.setitemtime(model.getOthertime());
                    String foundkey = getRef(position).getKey();
                    viewHolder.setExpireTime(model.getExpiretime());

                    TextView textView = viewHolder.view.findViewById(R.id.expiretime);
                    String posttime = textView.getText().toString();
                    if (!TextUtils.isEmpty(posttime)) {
                        long r = Long.parseLong(posttime);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        long ti = timestamp.getTime();

                        long diff = ti - r;

                        if (diff >= 1000 * 60 * 60 * 24 * 14) {
                            databaseothers2.child(foundkey).removeValue();
                        }
                    }

                    viewHolder.cardviewothers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            TextView uid = viewHolder.view.findViewById(R.id.otheruid);
                            TextView date = viewHolder.view.findViewById(R.id.timeoth);

                            String uidd = uid.getText().toString();
                            String datee = date.getText().toString();

                            Intent r = new Intent(getActivity(), Seer.class);
                            r.putExtra("Uid", uidd);
                            r.putExtra("Date", datee);
                            startActivity(r);

                        }
                    });


                }
            };

            otherrecyclerview.setAdapter(firebaseRecyclerAdapter6);


            databaseid2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {

                        databasesc2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {

                                    databasebook2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.exists()) {

                                                databaseele2.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (!dataSnapshot.exists()) {

                                                            databasedoc2.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (!dataSnapshot.exists()) {

                                                                        databaseothers2.addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                if (!dataSnapshot.exists()) {
                                                                                    TextView textView = view.findViewById(R.id.noitemfound);
                                                                                    textView.setVisibility(View.VISIBLE);
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            databaseid2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    databasesc2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            databasebook2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    databasedoc2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            databaseele2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    databaseothers2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            progressDialog.dismiss();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


        return view;
    }

    public static class FoundViewHolder extends RecyclerView.ViewHolder {

        View view;
        CardView cardviewid, cardviewsc, cardviewbook, cardviewelect, cardviewothers, cardviewdoc;

        public FoundViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            cardviewid = view.findViewById(R.id.card);
            cardviewsc = view.findViewById(R.id.cardsc);
            cardviewbook = view.findViewById(R.id.cardb);
            cardviewelect = view.findViewById(R.id.carde);
            cardviewdoc = view.findViewById(R.id.cardv);
            cardviewothers = view.findViewById(R.id.cardo);
        }


        public void setIdname(String name) {
            TextView textView = view.findViewById(R.id.idnametext);
            textView.setText(name);
        }


        public void setIdno(String name) {
            TextView textView = view.findViewById(R.id.idtext);
            textView.setText(name);
        }


        public void setIdtel(String name) {
            TextView textView = view.findViewById(R.id.telid);
            textView.setText(name);
        }

        public void setIdtime(String name) {
            TextView textView = view.findViewById(R.id.timeid);
            textView.setText(name);
        }


        public void setScname(String name) {
            TextView textView = view.findViewById(R.id.idnametext);
            textView.setText(name);
        }


        public void setregno(String name) {
            TextView textView = view.findViewById(R.id.idtext);
            textView.setText(name);
        }


        public void settelsc(String name) {
            TextView textView = view.findViewById(R.id.schid);
            textView.setText(name);
        }


        public void setsctime(String name) {
            TextView textView = view.findViewById(R.id.timesc);
            textView.setText(name);
        }

        public void setBookTitle(String name) {
            TextView textView = view.findViewById(R.id.idnametext);
            textView.setText(name);
        }


        public void setBooksubject(String name) {
            TextView textView = view.findViewById(R.id.idtext);
            textView.setText(name);
        }


        public void setbookTel(String name) {
            TextView textView = view.findViewById(R.id.bookuidid);
            textView.setText(name);
        }

        public void setbooktime(String name) {
            TextView textView = view.findViewById(R.id.timebook);
            textView.setText(name);
        }

        public void setdoctype(String name) {
            TextView textView = view.findViewById(R.id.idnametext);
            textView.setText(name);
        }


        public void setdocname(String name) {
            TextView textView = view.findViewById(R.id.idtext);
            textView.setText(name);
        }


        public void setdoctel(String name) {
            TextView textView = view.findViewById(R.id.tedocuid);
            textView.setText(name);
        }

        public void setdoctime(String name) {
            TextView textView = view.findViewById(R.id.timedoc);
            textView.setText(name);
        }

        public void seteletype(String name) {
            TextView textView = view.findViewById(R.id.idnametext);
            textView.setText(name);
        }


        public void setelemodel(String name) {
            TextView textView = view.findViewById(R.id.idtext);
            textView.setText(name);
        }


        public void seteletel(String name) {
            TextView textView = view.findViewById(R.id.electuid);
            textView.setText(name);
        }

        public void seteletime(String name) {
            TextView textView = view.findViewById(R.id.timeele);
            textView.setText(name);
        }

        public void setitem(String name) {
            TextView textView = view.findViewById(R.id.idnametext);
            textView.setText(name);
        }


        public void setitemdesc(String name) {
            TextView textView = view.findViewById(R.id.idtext);
            textView.setText(name);
        }

        public void setExpireTime(long to) {

            TextView textView = view.findViewById(R.id.expiretime);
            textView.setText(String.valueOf(to));
        }


        public void setitemtel(String name) {
            TextView textView = view.findViewById(R.id.otheruid);
            textView.setText(name);
        }

        public void setitemtime(String name) {
            TextView textView = view.findViewById(R.id.timeoth);
            textView.setText(name);
        }


    }

    private boolean IsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;

        } else {
            return false;
        }


    }

}
