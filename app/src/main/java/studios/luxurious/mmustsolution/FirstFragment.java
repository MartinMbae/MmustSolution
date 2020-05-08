package studios.luxurious.mmustsolution;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FirstFragment extends Fragment {
    private DatabaseReference databaseid, databasesc, databasebook, databaseele, databasedoc, databaseothers;
    private DatabaseReference databaseid2, databasesc2, databasebook2, databaseele2, databasedoc2, databaseothers2;
    private RecyclerView idrecyclerview, screcyclerview, bookrecyclerview, electrecyclerview, docrecyclerview, otherrecyclerview;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

           final View view = inflater.inflate(R.layout.page_3, container, false);

        databaseid = FirebaseDatabase.getInstance().getReference().child("Lost_IDs");
        databasesc = FirebaseDatabase.getInstance().getReference().child("Lost_School_IDs");
        databasebook = FirebaseDatabase.getInstance().getReference().child("Lost_Books");
        databaseele = FirebaseDatabase.getInstance().getReference().child("Lost_Electronics");
        databasedoc = FirebaseDatabase.getInstance().getReference().child("Lost_Documents");
        databaseothers = FirebaseDatabase.getInstance().getReference().child("Lost_Others");



        databaseid2 = FirebaseDatabase.getInstance().getReference().child("Found_IDs");
        databasesc2 = FirebaseDatabase.getInstance().getReference().child("Found_School_IDs");
        databasebook2 = FirebaseDatabase.getInstance().getReference().child("Found_Books");
        databaseele2 = FirebaseDatabase.getInstance().getReference().child("Found_Electronics");
        databasedoc2 = FirebaseDatabase.getInstance().getReference().child("Found_Documents");
        databaseothers2 = FirebaseDatabase.getInstance().getReference().child("Found_Others");

           final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        databaseid.keepSynced(true);
        databasesc.keepSynced(true);
        databasebook.keepSynced(true);
        databaseele.keepSynced(true);
        databasedoc.keepSynced(true);
        databaseothers.keepSynced(true);
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
        mAuth = FirebaseAuth.getInstance();

        if (!IsConnected()){

            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            Toast.makeText(getActivity(), "No Internet connection", Toast.LENGTH_SHORT).show();
        }
        if(mAuth.getCurrentUser() == null){
            progressDialog.dismiss();
        }
        if (isAdded()) {

            FirebaseRecyclerAdapter<Lost, LostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Lost, LostViewHolder>(
                    Lost.class,
                    R.layout.lostandfoundid,
                    LostViewHolder.class,
                    databaseid

            ) {
                @Override
                protected void populateViewHolder(final LostViewHolder viewHolderid, Lost model, final int positionid) {


                    viewHolderid.setIdName(model.getNameid());
                    viewHolderid.setIdNo(model.getIdnumber());
                    viewHolderid.settelid(model.getNumber());

                    viewHolderid.idclaim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mAuth.getCurrentUser().isAnonymous()) {
                                Snackbar.make(v, "Guest users are not allowed claim items", Snackbar.LENGTH_LONG).show();

                            }
                            else if(!IsConnected()) {

                                Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();

                            }
                            else
                             {

                                AlertDialog.Builder alertDialog;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                } else {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                }

                                TextView na = viewHolderid.view.findViewById(R.id.idnametext);
                                TextView id = viewHolderid.view.findViewById(R.id.idtext);
                                TextView tel = viewHolderid.view.findViewById(R.id.telid);
                                 final String s = na.getText().toString();
                                 final String str3 = id.getText().toString();
                                 final String telno = tel.getText().toString();
                                String str2 = "Do you claim to be " + s + "  of ID number " + str3 + "?";
                                alertDialog.setTitle("Confirm Ownership")

                                        .setMessage(str2)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
 
                                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                    long ti = timestamp.getTime();
                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(ti);
                                                    Date d = calendar.getTime();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", java.util.Locale.getDefault());
                                                    SimpleDateFormat simpleDateFormats = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());

                                                    String od = simpleDateFormat.format(d);
                                                    String ods = simpleDateFormats.format(d);
                                                    String f = ods + " at " + od;
                                                    String uid = mAuth.getCurrentUser().getUid();


                                                    String postkeyid = getRef(positionid).getKey();


                                                    DatabaseReference post = databaseid2.push();

                                                    post.child("iduid").setValue(uid);
                                                    post.child("idtime").setValue(f);
                                                    post.child("expiretime").setValue(ti);
                                                    post.child("idname").setValue(s);
                                                    post.child("idno").setValue(str3);

                                                    databaseid.child(postkeyid).removeValue();

                                                    Intent co = new Intent(getActivity(), Connectfound.class);
                                                    co.putExtra("Number", telno);
                                                     
                                                    startActivity(co);


                                                    

                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_menu_send)
                                        .show();
                            }
                        }
                    });
                }


            };

            idrecyclerview.setAdapter(firebaseRecyclerAdapter);


            FirebaseRecyclerAdapter<Lost, LostViewHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<Lost, LostViewHolder>(
                    Lost.class,
                    R.layout.lostandfoundschoolid,
                    LostViewHolder.class,
                    databasesc

            ) {
                @Override
                protected void populateViewHolder(final LostViewHolder viewHolder, Lost model, final int position) {


                    viewHolder.setScName(model.getNamesc());
                    viewHolder.setRegNo(model.getRegno());
                    viewHolder.setschtel(model.getNumber());

                    viewHolder.schclaim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mAuth.getCurrentUser().isAnonymous()) {
                                Snackbar.make(v, "Guest users are not allowed claim items", Snackbar.LENGTH_LONG).show();

                            }
                            else if(!IsConnected()) {

                                Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();

                            }
                            else {

                                AlertDialog.Builder alertDialog;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                } else {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                }

                                TextView na = viewHolder.view.findViewById(R.id.idnametexts);
                                TextView id = viewHolder.view.findViewById(R.id.idtexts);
                                TextView tel = viewHolder.view.findViewById(R.id.tel);
                                   final String s = na.getText().toString();
                                   final String str3 = id.getText().toString();
                                   final String telno = tel.getText().toString();
                                String str2 = "Do you claim to be " + s + "  Reg. number " + str3 + "?";
                                alertDialog.setTitle("Confirm Ownership")

                                        .setMessage(str2)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
 
                                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                    long ti = timestamp.getTime();

                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(ti);
                                                    Date d = calendar.getTime();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", java.util.Locale.getDefault());
                                                    SimpleDateFormat simpleDateFormats = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());

                                                    String od = simpleDateFormat.format(d);
                                                    String ods = simpleDateFormats.format(d);

                                                    String f = ods + " at " + od;

                                                    String uid = mAuth.getCurrentUser().getUid();


                                                    String postkey = getRef(position).getKey();


                                                    DatabaseReference post = databasesc2.push();

                                                    post.child("scuid").setValue(uid);
                                                    post.child("sctime").setValue(f);
                                                    post.child("scname").setValue(s);
                                                    post.child("expiretime").setValue(ti);
                                                    post.child("regno").setValue(str3);

                                                    databasesc.child(postkey).removeValue();

                                                    Intent co = new Intent(getActivity(), Connectfound.class);
                                                    co.putExtra("Number", telno);
                                                     
                                                    startActivity(co);


                                              

                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_menu_send)
                                        .show();

                            }
                        }
                    });
                }


            };

            screcyclerview.setAdapter(firebaseRecyclerAdapter2);


            FirebaseRecyclerAdapter<Lost, LostViewHolder> firebaseRecyclerAdapter3 = new FirebaseRecyclerAdapter<Lost, LostViewHolder>(
                    Lost.class,
                    R.layout.lostandfoundbooks,
                    LostViewHolder.class,
                    databasebook

            ) {
                @Override
                protected void populateViewHolder(final LostViewHolder viewHolder, Lost model, final int position) {


                    viewHolder.setBookTitle(model.getBooktitle());
                    viewHolder.setSubject(model.getSubject());
                    viewHolder.setBookPlace(model.getPlacebook());
                    viewHolder.setBookUnique(model.getUniquebook());
                    viewHolder.setbooktel(model.getNumber());

                    viewHolder.bookclaim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mAuth.getCurrentUser().isAnonymous()) {
                                Snackbar.make(v, "Guest users are not allowed claim items", Snackbar.LENGTH_LONG).show();

                            }
                            else if(!IsConnected()) {

                                Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();

                            } else {


                                AlertDialog.Builder alertDialog;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                } else {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                }

                                TextView na = viewHolder.view.findViewById(R.id.idnametextb);
                                TextView id = viewHolder.view.findViewById(R.id.idtextb);
                                TextView tel = viewHolder.view.findViewById(R.id.telbook);
                                   final String s = na.getText().toString();
                                   final String str3 = id.getText().toString();
                                   final String telno = tel.getText().toString();
                                String str2 = "Do you claim to be the owner of " + s + "  , " + str3 + "?";
                                alertDialog.setTitle("Confirm Ownership")

                                        .setMessage(str2)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
 
                                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                    long ti = timestamp.getTime();

                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(ti);
                                                    Date d = calendar.getTime();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", java.util.Locale.getDefault());
                                                    SimpleDateFormat simpleDateFormats = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());

                                                    String od = simpleDateFormat.format(d);
                                                    String ods = simpleDateFormats.format(d);

                                                    String f = ods + " at " + od;


                                                    String uid = mAuth.getCurrentUser().getUid();


                                                    String postkey = getRef(position).getKey();


                                                    DatabaseReference post = databasebook2.push();

                                                    post.child("bookuid").setValue(uid);
                                                    post.child("booktime").setValue(f);
                                                    post.child("expiretime").setValue(ti);
                                                    post.child("booktitle").setValue(s);
                                                    post.child("subject").setValue(str3);

                                                    databasebook.child(postkey).removeValue();

                                                    Intent co = new Intent(getActivity(), Connectfound.class);
                                                    co.putExtra("Number", telno);
                                                     
                                                    startActivity(co);


                                             

                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_menu_send)
                                        .show();

                            }
                        }
                    });
                }


            };

            bookrecyclerview.setAdapter(firebaseRecyclerAdapter3);


            FirebaseRecyclerAdapter<Lost, LostViewHolder> firebaseRecyclerAdapter4 = new FirebaseRecyclerAdapter<Lost, LostViewHolder>(
                    Lost.class,
                    R.layout.lostandfoundelectronics,
                    LostViewHolder.class,
                    databaseele

            ) {
                @Override
                protected void populateViewHolder(final LostViewHolder viewHolder, Lost model, final int position) {


                    viewHolder.setlEctronivType(model.getElectronictype());
                    viewHolder.setModel(model.getModel());
                    viewHolder.setElectronicPlace(model.getPlaceelectronic());
                    viewHolder.setElectronicUnique(model.getUniqueelectronic());
                    viewHolder.setelectel(model.getNumber());

                    viewHolder.electclaim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (mAuth.getCurrentUser().isAnonymous()) {
                                Snackbar.make(v, "Guest users are not allowed claim items", Snackbar.LENGTH_LONG).show();

                            }
                            else if(!IsConnected()) {

                                Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();

                            } else {

                                AlertDialog.Builder alertDialog;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                } else {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                }

                                TextView na = viewHolder.view.findViewById(R.id.idnametexte);
                                TextView id = viewHolder.view.findViewById(R.id.idtexte);
                                TextView tel = viewHolder.view.findViewById(R.id.telele);
                                   final String s = na.getText().toString();
                                   final String str3 = id.getText().toString();
                                   final String telno = tel.getText().toString();
                                String str2 = "Do you claim to be the owner of  " + s + " , " + str3 + "?";
                                alertDialog.setTitle("Confirm Ownership")

                                        .setMessage(str2)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
 
                                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                    long ti = timestamp.getTime();

                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(ti);
                                                    Date d = calendar.getTime();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", java.util.Locale.getDefault());
                                                    SimpleDateFormat simpleDateFormats = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());

                                                    String od = simpleDateFormat.format(d);
                                                    String ods = simpleDateFormats.format(d);

                                                    String f = ods + " at " + od;

                                                    String uid = mAuth.getCurrentUser().getUid();


                                                    String postkey = getRef(position).getKey();


                                                    DatabaseReference post = databaseele2.push();

                                                    post.child("electuid").setValue(uid);
                                                    post.child("electime").setValue(f);
                                                    post.child("electtype").setValue(s);
                                                    post.child("model").setValue(str3);
                                                    post.child("expiretime").setValue(ti);

                                                    databaseele.child(postkey).removeValue();

                                                    Intent co = new Intent(getActivity(), Connectfound.class);
                                                    co.putExtra("Number", telno);
                                                     
                                                    startActivity(co);


                                                

                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_menu_send)
                                        .show();

                            }
                        }
                    });
                }


            };

            electrecyclerview.setAdapter(firebaseRecyclerAdapter4);


            FirebaseRecyclerAdapter<Lost, LostViewHolder> firebaseRecyclerAdapter5 = new FirebaseRecyclerAdapter<Lost, LostViewHolder>(
                    Lost.class,
                    R.layout.lostandfounddocuments,
                    LostViewHolder.class,
                    databasedoc

            ) {
                @Override
                protected void populateViewHolder(final LostViewHolder viewHolder, Lost model, final int position) {

                    viewHolder.setlDocumentType(model.getDocumenttype());
                    viewHolder.setnameDocument(model.getNamedocument());
                    viewHolder.setUniquedoc(model.getUniquedocument());
                    viewHolder.setdoctel(model.getNumber());

                    viewHolder.docclaim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mAuth.getCurrentUser().isAnonymous()) {
                                Snackbar.make(v, "Guest users are not allowed claim items", Snackbar.LENGTH_LONG).show();

                            }
                            else if(!IsConnected()) {

                                Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();

                            } else {

                                AlertDialog.Builder alertDialog;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                } else {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                }

                                TextView na = viewHolder.view.findViewById(R.id.idnametextd);
                                TextView id = viewHolder.view.findViewById(R.id.idtextd);
                                TextView tel = viewHolder.view.findViewById(R.id.teldoc);
                                   final String s = na.getText().toString();
                                   final String str3 = id.getText().toString();
                                   final String telno = tel.getText().toString();
                                String str2 = "Do you claim to be the owner of  " + s + "  , " + str3 + "?";
                                alertDialog.setTitle("Confirm Ownership")

                                        .setMessage(str2)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
 
                                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                    long ti = timestamp.getTime();

                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(ti);
                                                    Date d = calendar.getTime();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", java.util.Locale.getDefault());
                                                    SimpleDateFormat simpleDateFormats = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());

                                                    String od = simpleDateFormat.format(d);
                                                    String ods = simpleDateFormats.format(d);

                                                    String f = ods + " at " + od;


                                                    String uid = mAuth.getCurrentUser().getUid();


                                                    String postkey = getRef(position).getKey();


                                                    DatabaseReference post = databasedoc2.push();

                                                    post.child("docuid").setValue(uid);
                                                    post.child("doctime").setValue(f);
                                                    post.child("doctype").setValue(s);
                                                    post.child("docname").setValue(str3);
                                                    post.child("expiretime").setValue(ti);

                                                    databasedoc.child(postkey).removeValue();

                                                    Intent co = new Intent(getActivity(), Connectfound.class);
                                                    co.putExtra("Number", telno);
                                                     
                                                    startActivity(co);


                                               

                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_menu_send)
                                        .show();

                            }
                        }
                    });
                }


            };

            docrecyclerview.setAdapter(firebaseRecyclerAdapter5);


            FirebaseRecyclerAdapter<Lost, LostViewHolder> firebaseRecyclerAdapter6 = new FirebaseRecyclerAdapter<Lost, LostViewHolder>(
                    Lost.class,
                    R.layout.lostandfoundothers,
                    LostViewHolder.class,
                    databaseothers

            ) {
                @Override
                protected void populateViewHolder(final LostViewHolder viewHolder, Lost model, final int position) {

                    viewHolder.setItem(model.getItem());
                    viewHolder.setItemDescription(model.getItemdescription());
                    viewHolder.setothertel(model.getNumber());

                    viewHolder.otherclaim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mAuth.getCurrentUser().isAnonymous()) {
                                Snackbar.make(v, "Guest users are not allowed claim items", Snackbar.LENGTH_LONG).show();

                            }
                            else if(!IsConnected()) {

                                Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();

                            } else {


                                AlertDialog.Builder alertDialog;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                } else {
                                    alertDialog = new AlertDialog.Builder(getActivity());
                                }

                                TextView na = viewHolder.view.findViewById(R.id.idnametexto);
                                TextView id = viewHolder.view.findViewById(R.id.idtexto);
                                TextView tel = viewHolder.view.findViewById(R.id.telothers);
                                 final String s = na.getText().toString();
                                 final String str3 = id.getText().toString();
                                 final String telno = tel.getText().toString();
                                String str2 = "Do you claim to be owner of  " + s + " , " + str3 + "?";
                                alertDialog.setTitle("Confirm Ownership")

                                        .setMessage(str2)
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
 
                                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                                     long ti = timestamp.getTime();
                                                    Calendar calendar = Calendar.getInstance();
                                                    calendar.setTimeInMillis(ti);
                                                    Date d = calendar.getTime();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", java.util.Locale.getDefault());
                                                    SimpleDateFormat simpleDateFormats = new SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());

                                                    String od = simpleDateFormat.format(d);
                                                    String ods = simpleDateFormats.format(d);
                                                     String f = ods + " at " + od;
                                                     String uid = mAuth.getCurrentUser().getUid();
                                                     final String postkey = getRef(position).getKey();
                                                    DatabaseReference post = databaseothers2.push();
                                                    
                                                   
                                                            post.child("otheruid").setValue(uid);
                                                            post.child("othertime").setValue(f);
                                                            post.child("othername").setValue(s);
                                                            post.child("expiretime").setValue(ti);
                                                            post.child("otherno").setValue(str3);

                                                databaseothers.child(postkey).removeValue();


                                                    Intent co = new Intent(getActivity(), Connectfound.class);
                                                    co.putExtra("Number", telno);
                                                    startActivity(co);




                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_menu_send)
                                        .show();

                            }
                        }
                    });
                }


            };

            otherrecyclerview.setAdapter(firebaseRecyclerAdapter6);


            databaseid.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {

                        databasesc.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {

                                    databasebook.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.exists()) {

                                                databaseele.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (!dataSnapshot.exists()) {

                                                            databasedoc.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (!dataSnapshot.exists()) {

                                                                        databaseothers.addValueEventListener(new ValueEventListener() {
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


            databaseid.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    databasesc.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            databasebook.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    databasedoc.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            databaseele.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    databaseothers.addListenerForSingleValueEvent(new ValueEventListener() {
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


    public static class LostViewHolder extends RecyclerView.ViewHolder {

        View view;
        CardView idclaim,schclaim,bookclaim,docclaim,electclaim,otherclaim;

        public LostViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            idclaim = view.findViewById(R.id.card);
            schclaim = view.findViewById(R.id.cardh);
            bookclaim = view.findViewById(R.id.cardb);
            docclaim = view.findViewById(R.id.cardv);
            electclaim = view.findViewById(R.id.carde);
            otherclaim = view.findViewById(R.id.cardo);

        }

        public void setIdName(String name) {

            TextView d = view.findViewById(R.id.idnametext);
            d.setText(name);
        }

        public void setIdNo(String name) {

            TextView d = view.findViewById(R.id.idtext);
            d.setText(name);
        }

        public void setScName(String name) {

            TextView d = view.findViewById(R.id.idnametexts);
            d.setText(name);
        }

        public void setRegNo(String name) {

            TextView d = view.findViewById(R.id.idtexts);
            d.setText(name);
        }

        public void setBookTitle(String name) {

            TextView d = view.findViewById(R.id.idnametextb);
            d.setText(name);
        }

        public void setSubject(String name) {

            TextView d = view.findViewById(R.id.idtextb);
            d.setText(name);
        }

        public void setBookPlace(String name) {

            TextView d = view.findViewById(R.id.placefound);
            d.setText(name);
        }

        public void setBookUnique(String name) {

            TextView d = view.findViewById(R.id.unique);
            d.setText(name);
        }


        public void setlEctronivType(String name) {

            TextView d = view.findViewById(R.id.idnametexte);
            d.setText(name);
        }

        public void setModel(String name) {

            TextView d = view.findViewById(R.id.idtexte);
            d.setText(name);
        }

        public void setElectronicPlace(String name) {

            TextView d = view.findViewById(R.id.placefound);
            d.setText(name);
        }

        public void setElectronicUnique(String name) {

            TextView d = view.findViewById(R.id.unique);
            d.setText(name);
        }

        public void setlDocumentType(String name) {

            TextView d = view.findViewById(R.id.idnametextd);
            d.setText(name);
        }

        public void setnameDocument(String name) {

            TextView d = view.findViewById(R.id.idtextd);
            d.setText(name);
        }

        public void setUniquedoc(String name) {

            TextView d = view.findViewById(R.id.unique);
            d.setText(name);
        }

        public void setItem(String name) {

            TextView d = view.findViewById(R.id.idnametexto);
            d.setText(name);
        }

        public void setItemDescription(String name) {

            TextView d = view.findViewById(R.id.idtexto);
            d.setText(name);
        }


        public void settelid(String name) {

            TextView d = view.findViewById(R.id.telid);
            d.setText(name);
        }

        public void setbooktel(String name) {

            TextView d = view.findViewById(R.id.telbook);
            d.setText(name);
        }

        public void setschtel(String name) {

            TextView d = view.findViewById(R.id.tel);
            d.setText(name);
        }

        public void setdoctel(String name) {

            TextView d = view.findViewById(R.id.teldoc);
            d.setText(name);
        }

        public void setelectel(String name) {

            TextView d = view.findViewById(R.id.telele);
            d.setText(name);
        }

        public void setothertel(String name) {

            TextView d = view.findViewById(R.id.telothers);
            d.setText(name);
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
