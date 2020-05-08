package studios.luxurious.mmustsolution;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import studios.luxurious.mmustsolution.Utils.FAQ_item;


public class FaqActivity extends AppCompatActivity {

    Faqdapter faq_adapter;

    ArrayList<FAQ_item> faq_items;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        faq_items = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);

        faq_items.add(new FAQ_item("How Much Is The Accommodation Fee?", "The University accommodation fee ranges from Kshs 5000/- to Kshs 8000/- depending on the number of beds per room per semester."));
        faq_items.add(new FAQ_item("Do PSSP and Jab Students Pay The Same Accomodation Fee?", "Yes. Both the privately sponsored and the government sponsored students pay the same accommodation fee."));
        faq_items.add(new FAQ_item("Who Is Eligible To Apply For A Hostel?", "The University provides an online platform where ONLY FIRST YEARS can login and book for accommodation."));
        faq_items.add(new FAQ_item("Does The University Monitor The Private Rented Accommodation It Advertises To Its Students?", "Yes. The University oversees all the private hostels to ensure that they provide a conducive environment for students in terms of security, cleanliness and other general aspects before we allow them to advertise to our students."));
        faq_items.add(new FAQ_item("What support does the University offer to ensure that students get work?", "Employability is embedded into the wide range of degree courses provided by the University. We also have a Directorate of Career Services which provides support with job application. MMUST provides its students with all that they require to enter the world of work."));
        faq_items.add(new FAQ_item("Who can use the University Library?", "The teaching, non-teaching and students are the bonafide members who have a right to read and borrow academic material from the University Library."));
        faq_items.add(new FAQ_item("What Is The Fee Payment Mode?", "Fees is paid Into the following University Accounts:\n" +
                "\n" +
                "KCB Bank - 1101922109  - Kakamega Branch" +
                "\n" +
                "Equity Bank - 0500294636103  - Kakamega Branch " +
                "\n" +
                "Equity Bank - 1650264152539  - Kakuma Branch" +
                "\n"));
        faq_items.add(new FAQ_item("Where can I find the Fee Structure?", "The fee structure can be downloaded from the University website. Alternatively, one can acquire the fee structure at the Office of the Registrar (Academic Affairs)."));
        faq_items.add(new FAQ_item("Who qualifies to sit for University Examinations?", "Only bona fide students are allowed to sit for University examinations. These are students who have paid fees in full and have reported online and registered for the prescribed semester courses."));
        faq_items.add(new FAQ_item("Does the University offer Part Time Courses?", "Yes. Part time courses are mostly available in post graduate programmes. Different Schools provide different schedules for part time courses depending on the preferences of the students."));
        faq_items.add(new FAQ_item("How Do I Channel a Complaint Or Compliment?", "Any complaint or compliment can be addressed to:\n" +
                "\n" +
                "The Customer Care and Call Centre located at the New Administration Block ABA 001\n" +
                "Call the Toll free number on 0572505222\n" +
                "Ombudsman Office at the University or\n" +
                "Email :  info@mmust.ac.ke\n" +
                "              pr@mmust.ac.ke\n" +
                "              ombudsman@mmust.ac.ke"));
        faq_items.add(new FAQ_item("What are the Key Dates in the University Academic Calendar?", "We have in-takes in January, May and September of each year. Graduation is normally conducted in December and only on special occasion can it be conducted in July. KUCCPS students are enrolled between August and September of each year. Students go for long holidays between May and September."));
        faq_items.add(new FAQ_item("How long does it take to get a reply after a Successful Course Application?", "A maximum of 14 working days."));
        faq_items.add(new FAQ_item("What is the procedure of transfer from the University to other Institutions?", "Procedure for Transfer from MMUST to Other Universities\n" +
                "\n" +
                "Government Sponsored Students\n" +
                "• The transfer is done online through the Kenya Universities and Colleges Central Placement Service (KUCCPS) website.\n" +
                "• The student logs in to the KUCCPS student portal and keys in his/her KCSE index number, KCSE year and password.\n" +
                "• The student confirms the cut off points of the Course he/she intends to pursue at the University he/ she wishes to join.\n" +
                "• If he/she qualifies, he/she keys in the program code.\n" +
                "• A message is given to the student informing him/her if he/she qualifies for the program or not.\n" +
                "• If he/she qualifies, a payment of Kshs 1,000/- is paid to KUCCPS.\n" +
                "• The University the student wishes to transfer to will look at its capacity to determine if it will either accept or reject the application.\n" +
                "• The University the student was earlier admitted to/was to be earlier admitted to will either accept or rejects his/her release.\n" +
                "• If accepted, the information will be sent to KUCCPS.\n" +
                "• KUCCPS will write a transfer letter to the student.\n" +
                "• The current University will use the transfer letter to write the student an admission letter.\n" +
                "\n" +
                "Privately Sponsored Students\n" +
                "• The student writes a letter to the Vice Chancellor of the University he/she wishes to transfer to through the Vice Chancellor of the University he/she is currently in.\n" +
                "• The letter will be received at the Registrar (Academic Affairs) Office to ascertain whether the student qualifies.\n" +
                "• The Registrar (Academic Affairs) Office will advice the Vice Chancellor if the student qualifies and if there is a capacity for the said course.\n" +
                "• The student attaches his/her Admission Letter he/she was given when he was admitted in to the University before requesting for transfer.\n" +
                "• The student has to attach a clearance form/letter from the former University and his/her academic testimonials.\n" +
                "• After verification of the above mentioned, the student will be given an Admission Letter\n" +
                "\n" +
                "\n" +
                "Procedure for Transfer from Other Universities to MMUST\n" +
                "Government Sponsored Students\n" +
                "• The transfer is done online through Kenya Universities and Colleges Central Placement Service (KUCCPS) website.\n" +
                "• The student logs in to the KUCCPS student portal and keys in his/her KCSE index number, KCSE year and password.\n" +
                "• The student confirms the cut off points of the Course he/she intends to pursue in the University he/ she wishes to join.\n" +
                "• If he/she qualifies, he/she keys in the program code.\n" +
                "• A message is given to the student informing him/her if he/she qualifies for the program or not.\n" +
                "• If he/she qualifies, a payment of Kshs 1,000 is paid to KUCCPS.\n" +
                "• The University the student wishes to transfer to will look at its capacity to determine if it will either accept or reject the application.\n" +
                "• The University the student was earlier admitted to/was to be earlier admitted to will either accept or rejects his/her release.\n" +
                "• If accepted, the information will be sent to KUCCPS.\n" +
                "• KUCCPS will write a transfer letter to the student.\n" +
                "• The current University will use the transfer letter to write the student an Admission Letter.\n" +
                "\n" +
                "\n" +
                "Privately Sponsored Students\n" +
                "• The student writes a letter to the Vice Chancellor of the University he/she wishes to transfer to through the Vice Chancellor of the University he/she is currently in.\n" +
                "• The letter will be received in the Registrar (Academic Affairs) Office to ascertain whether the student qualifies.\n" +
                "• The Registrar (Academic Affairs) Office will advise the Vice Chancellor if the student qualifies and if there is a capacity for the said Course.\n" +
                "• The student attaches Admission Letter he/she was given when he was admitted in to the University before requesting for transfer.\n" +
                "• The student has to attach clearance form/letter from the former University and his/her academic testimonials.\n" +
                "• After verification of the above mentioned, the student will be given an Admission Letter.\n" +
                "\n" +
                " "));
        faq_items.add(new FAQ_item("Where should the students pick their transcripts?", "School level provisional results are picked at the various Schools whereas official transcripts are picked from the Office of the Registrar (Academic Affairs.)"));
        faq_items.add(new FAQ_item("What is The Timeline for Correcting Certificates with Errors?", "When picking a certificate, one is asked to check instantly to ascertain if there are any errors. If any, corrections are made within a period of one month, free of charge. However, if one leaves the premises of the University with erroneous certificates and comes back later for correction, they attract a penalty."));
        faq_items.add(new FAQ_item("Who is eligible to apply for Internship at the University?", "The University provides internship opportunities to its alumni only. Conversely, for courses not offered at the University, we recruit the most competent interns from other universities."));
        faq_items.add(new FAQ_item("What is the Course Application Procedure and Requirements?", "Requirements for Online Application are:\n" +
                "\n" +
                "A valid Personal Email Address and Password for the student.\n" +
                "Scanned application fee payment slip.\n" +
                "Scanned Academic Testimonials (e.g. Results Slips, Certificates etc.)\n" +
                "Procedure for Application.\n" +
                "\n" +
                "Visit  http://application.mmust.ac.ke/\n" +
                " Create User Account. Tip: Use the same password as that of email.\n" +
                "Verification of User Account\n" +
                "Login to your email address\n" +
                "Click on the link to verify account\n" +
                "Application of course\n" +
                "Login to your user account(Enter email address and password)\n" +
                "Follow the instructions by filling all appropriate information\n" +
                " Complete the application process\n" +
                "Print an acknowledgment slip"));
        faq_items.add(new FAQ_item("Does the University provide Scholarships to Students?", "Yes. Masters and PhD students are allowed to teach some units within the University. The stipend they receive is used to cater for their academic requirements."));
        faq_items.add(new FAQ_item("Does The University offer Short and Bridging Courses?", "The University offers short courses ranging from 5 days - 4 months. Currently, we do not provide bridging programmes."));
        faq_items.add(new FAQ_item(" Does the University admit International Students?", "Yes. The University does admit qualified International students who have applied and have their certificates equated with the Kenyan education system, that is, Commission for University Education (CUE), Kenya National Examination Council (KNEC) and Kenya National Qualifications Authority (KNQA)."));
        faq_items.add(new FAQ_item("What Is The MMUST Talent Scholarship?", "This is an initiative by the University to give back to the society through providing sponsorship to the talented students in the various sports and entertainment programmes."));


        faq_adapter = new Faqdapter(faq_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(faq_adapter);


    }
}
