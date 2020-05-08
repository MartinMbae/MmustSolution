package studios.luxurious.mmustsolution;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class SecondFragment extends Fragment {

    Spinner spinner;
    RelativeLayout layoutId,layoutSc,layoutBook,layoutele,layoutIdothers,layoutIddoc;
    ProgressDialog progressDialog;
    DatabaseReference databaseid,databasesc,databasebook,databaseele,databasedoc,databaseothers;
    FirebaseAuth mAuth;

    Button cardId,cardSc,cardBook,cardEle,cardOthers,cardDoc;
    SharedPref sharedPref;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup containerr,
                             Bundle savedInstanceState) {


        final View container = inflater.inflate(R.layout.activity_post_lost2, containerr, false);

        if (isAdded()) {

            sharedPref = new SharedPref(getActivity());

            spinner = container.findViewById(R.id.spinner);
            layoutId = container.findViewById(R.id.nationalid);
            layoutSc = container.findViewById(R.id.schoolid);
            layoutBook = container.findViewById(R.id.books);
            layoutele = container.findViewById(R.id.electonics);
            layoutIdothers = container.findViewById(R.id.others);
            layoutIddoc = container.findViewById(R.id.documents);
            cardId = container.findViewById(R.id.card);
            cardSc = container.findViewById(R.id.card2);
            cardBook = container.findViewById(R.id.cardb);
            cardEle = container.findViewById(R.id.cardbe);
            cardDoc = container.findViewById(R.id.card3);
            cardOthers = container.findViewById(R.id.cardm);
            progressDialog = new ProgressDialog(getActivity());
            mAuth = FirebaseAuth.getInstance();

            databaseid = FirebaseDatabase.getInstance().getReference().child("Lost_IDs");
            databasesc = FirebaseDatabase.getInstance().getReference().child("Lost_School_IDs");
            databasebook = FirebaseDatabase.getInstance().getReference().child("Lost_Books");
            databaseele = FirebaseDatabase.getInstance().getReference().child("Lost_Electronics");
            databasedoc = FirebaseDatabase.getInstance().getReference().child("Lost_Documents");
            databaseothers = FirebaseDatabase.getInstance().getReference().child("Lost_Others");


            cardId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedPref.getIsGuest()) {
                        Snackbar.make(v,"Guest users are not allowed to add posts",Snackbar.LENGTH_LONG).show();
                    }else {
                    EditText name, idno, num;
                    name = container.findViewById(R.id.w);
                    idno = container.findViewById(R.id.a);
                    num = container.findViewById(R.id.ww);

                    String namee = name.getText().toString();
                    String idnoo = idno.getText().toString();
                    String numm = num.getText().toString();

                    if (!TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo) && !TextUtils.isEmpty(numm)) {

                        progressDialog.setMessage("Sending...");
                        progressDialog.show();


                        DatabaseReference post = databaseid.push();

                        post.child("nameid").setValue(namee);
                        post.child("idnumber").setValue(idnoo);
                        post.child("number").setValue(numm);

                        progressDialog.dismiss();

                    } else if (!TextUtils.isEmpty(namee) && TextUtils.isEmpty(idnoo)) {
                        Toast.makeText(getActivity(), "ID Number can't be empty", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo)) {
                        Toast.makeText(getActivity(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Required fields are empty", Toast.LENGTH_SHORT).show();
                    }
                    layoutId.setVisibility(View.INVISIBLE);


                    Intent r = new Intent(getActivity(), LostAndFound.class);
                    r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(r);


                }
                }
            });
            cardSc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedPref.getIsGuest()) {
                        Snackbar.make(v, "Guest users are not allowed to add posts", Snackbar.LENGTH_LONG).show();

                    } else {
                        EditText name, idno, num;
                        name = container.findViewById(R.id.w2);
                        idno = container.findViewById(R.id.a2);
                        num = container.findViewById(R.id.ww2);

                        String namee = name.getText().toString();
                        String idnoo = idno.getText().toString();
                        String numm = num.getText().toString();

                        if (!TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo) && !TextUtils.isEmpty(numm)) {

                            progressDialog.setMessage("Sending...");
                            progressDialog.show();


                            DatabaseReference post = databasesc.push();

                            post.child("namesc").setValue(namee);
                            post.child("regno").setValue(idnoo);
                            post.child("number").setValue(numm);

                            progressDialog.dismiss();

                        } else if (!TextUtils.isEmpty(namee) && TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Registration Number can't be empty", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Required fields are empty", Toast.LENGTH_SHORT).show();
                        }
                        layoutSc.setVisibility(View.INVISIBLE);
                        Intent r = new Intent(getActivity(), LostAndFound.class);
                        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(r);
                    }
                }
            });

            cardBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedPref.getIsGuest()) {
                        Snackbar.make(v, "Guest users are not allowed to add posts", Snackbar.LENGTH_LONG).show();

                    } else {
                        EditText name, idno, unique, place, num;
                        name = container.findViewById(R.id.wb);
                        idno = container.findViewById(R.id.ab);
                        place = container.findViewById(R.id.wbbb);
                        unique = container.findViewById(R.id.abb);
                        num = container.findViewById(R.id.ww4);

                        String namee = name.getText().toString();
                        String idnoo = idno.getText().toString();
                        String uniquee = unique.getText().toString();
                        String plac = place.getText().toString();
                        String numm = num.getText().toString();

                        if (!TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo) && !TextUtils.isEmpty(plac) && !TextUtils.isEmpty(uniquee) && !TextUtils.isEmpty(numm)) {

                            progressDialog.setMessage("Sending...");
                            progressDialog.show();


                            DatabaseReference post = databasebook.push();

                            post.child("booktitle").setValue(namee);
                            post.child("subject").setValue(idnoo);
                            post.child("placebook").setValue(plac);
                            post.child("uniquebook").setValue(uniquee);
                            post.child("number").setValue(numm);

                            progressDialog.dismiss();

                        } else if (!TextUtils.isEmpty(namee) && TextUtils.isEmpty(plac)) {
                            Toast.makeText(getActivity(), "Place Found can't be empty", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Book Title can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Some of required fields are empty", Toast.LENGTH_SHORT).show();
                        }

                        layoutBook.setVisibility(View.INVISIBLE);
                        Intent r = new Intent(getActivity(), LostAndFound.class);
                        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(r);
                    }
                }
            });

            cardEle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedPref.getIsGuest()) {
                        Snackbar.make(v, "Guest users are not allowed to add posts", Snackbar.LENGTH_LONG).show();

                    } else {
                        EditText name, idno, unique, place, num;
                        name = container.findViewById(R.id.wbe);
                        idno = container.findViewById(R.id.abe);
                        place = container.findViewById(R.id.wbbbe);
                        unique = container.findViewById(R.id.abbe);
                        num = container.findViewById(R.id.ww5);

                        String namee = name.getText().toString();
                        String idnoo = idno.getText().toString();
                        String uniquee = unique.getText().toString();
                        String plac = place.getText().toString();
                        String numm = num.getText().toString();

                        if (!TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo) && !TextUtils.isEmpty(plac) && !TextUtils.isEmpty(uniquee) && !TextUtils.isEmpty(numm)) {

                            progressDialog.setMessage("Sending...");
                            progressDialog.show();


                            DatabaseReference post = databaseele.push();

                            post.child("electronictype").setValue(namee);
                            post.child("model").setValue(idnoo);
                            post.child("placeelectronic").setValue(plac);
                            post.child("uniqueelectronic").setValue(uniquee);
                            post.child("number").setValue(numm);

                            progressDialog.dismiss();

                        } else if (!TextUtils.isEmpty(namee) && TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Model can't be empty", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Electronic Type can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Some of required fields are empty", Toast.LENGTH_SHORT).show();
                        }
                        layoutele.setVisibility(View.INVISIBLE);
                        Intent r = new Intent(getActivity(), LostAndFound.class);
                        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(r);
                    }
                }
            });

            cardDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedPref.getIsGuest()) {
                        Snackbar.make(v,"Guest users are not allowed to add posts",Snackbar.LENGTH_LONG).show();

                    }else {
                        EditText name, idno, unique, num;
                        name = container.findViewById(R.id.w3);
                        idno = container.findViewById(R.id.a3);
                        unique = container.findViewById(R.id.a32);
                        num = container.findViewById(R.id.ww3);

                        String namee = name.getText().toString();
                        String idnoo = idno.getText().toString();
                        String uniquee = unique.getText().toString();
                        String numm = num.getText().toString();

                        if (!TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo) && !TextUtils.isEmpty(uniquee) && !TextUtils.isEmpty(numm)) {

                            progressDialog.setMessage("Sending...");
                            progressDialog.show();


                            DatabaseReference post = databasedoc.push();

                            post.child("documenttype").setValue(namee);
                            post.child("namedocument").setValue(idnoo);
                            post.child("uniquedocument").setValue(uniquee);
                            post.child("number").setValue(numm);

                            progressDialog.dismiss();

                        } else if (!TextUtils.isEmpty(namee) && TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Name on Document can't be empty", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Type of Document can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Some of required fields are empty", Toast.LENGTH_SHORT).show();
                        }
                        layoutIddoc.setVisibility(View.INVISIBLE);
                        Intent r = new Intent(getActivity(), LostAndFound.class);
                        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(r);

                    }
                }
            });

            cardOthers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedPref.getIsGuest()) {
                        Snackbar.make(v,"Guest users are not allowed to add posts",Snackbar.LENGTH_LONG).show();

                    }else {


                        EditText name, idno, num;

                        name = container.findViewById(R.id.wm);
                        idno = container.findViewById(R.id.am);
                        num = container.findViewById(R.id.ww6);

                        String namee = name.getText().toString();
                        String idnoo = idno.getText().toString();
                        String numm = num.getText().toString();

                        if (!TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo) && !TextUtils.isEmpty(numm)) {

                            progressDialog.setMessage("Sending...");
                            progressDialog.show();


                            DatabaseReference post = databaseothers.push();

                            post.child("item").setValue(namee);
                            post.child("itemdescription").setValue(idnoo);
                            post.child("number").setValue(numm);

                            progressDialog.dismiss();

                        } else if (!TextUtils.isEmpty(namee) && TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Item must be described", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(namee) && !TextUtils.isEmpty(idnoo)) {
                            Toast.makeText(getActivity(), "Item can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Required fields are empty", Toast.LENGTH_SHORT).show();
                        }
                        layoutIdothers.setVisibility(View.INVISIBLE);
                        Intent r = new Intent(getActivity(), LostAndFound.class);
                        r.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(r);

                    }
                }
            });

            List<String> cate = new ArrayList<>();
            cate.add("Chose Category");
            cate.add("National ID");
            cate.add("School ID");
            cate.add("Document");
            cate.add("Books");
            cate.add("Electronics");
            cate.add("Others");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, cate);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position) {

                        case 0:

                            layoutId.setVisibility(View.INVISIBLE);
                            layoutSc.setVisibility(View.INVISIBLE);
                            layoutIdothers.setVisibility(View.INVISIBLE);
                            layoutIddoc.setVisibility(View.INVISIBLE);
                            layoutBook.setVisibility(View.INVISIBLE);
                            layoutele.setVisibility(View.INVISIBLE);

                            break;
                        case 1:

                            layoutId.setVisibility(View.VISIBLE);
                            layoutSc.setVisibility(View.INVISIBLE);
                            layoutIdothers.setVisibility(View.INVISIBLE);
                            layoutIddoc.setVisibility(View.INVISIBLE);
                            layoutBook.setVisibility(View.INVISIBLE);
                            layoutele.setVisibility(View.INVISIBLE);

                            break;
                        case 2:

                            layoutId.setVisibility(View.INVISIBLE);
                            layoutSc.setVisibility(View.VISIBLE);
                            layoutIdothers.setVisibility(View.INVISIBLE);
                            layoutIddoc.setVisibility(View.INVISIBLE);
                            layoutBook.setVisibility(View.INVISIBLE);
                            layoutele.setVisibility(View.INVISIBLE);

                            break;

                        case 3:

                            layoutId.setVisibility(View.INVISIBLE);
                            layoutSc.setVisibility(View.INVISIBLE);
                            layoutIdothers.setVisibility(View.INVISIBLE);
                            layoutIddoc.setVisibility(View.VISIBLE);
                            layoutBook.setVisibility(View.INVISIBLE);
                            layoutele.setVisibility(View.INVISIBLE);

                            break;
                        case 4:

                            layoutId.setVisibility(View.INVISIBLE);
                            layoutSc.setVisibility(View.INVISIBLE);
                            layoutIdothers.setVisibility(View.INVISIBLE);
                            layoutIddoc.setVisibility(View.INVISIBLE);
                            layoutBook.setVisibility(View.VISIBLE);
                            layoutele.setVisibility(View.INVISIBLE);

                            break;

                        case 5:

                            layoutId.setVisibility(View.INVISIBLE);
                            layoutSc.setVisibility(View.INVISIBLE);
                            layoutIdothers.setVisibility(View.INVISIBLE);
                            layoutIddoc.setVisibility(View.INVISIBLE);
                            layoutBook.setVisibility(View.INVISIBLE);
                            layoutele.setVisibility(View.VISIBLE);


                            break;
                        case 6:

                            layoutId.setVisibility(View.INVISIBLE);
                            layoutSc.setVisibility(View.INVISIBLE);
                            layoutIdothers.setVisibility(View.VISIBLE);
                            layoutIddoc.setVisibility(View.INVISIBLE);
                            layoutBook.setVisibility(View.INVISIBLE);
                            layoutele.setVisibility(View.INVISIBLE);

                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
            return container;



    }

}
