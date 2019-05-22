package com.kulikulad.MessCul.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.adapters.SectionPagerAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        var sectionAdapter: SectionPagerAdapter? = null;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar!!.title = "Dashboard";

        sectionAdapter = SectionPagerAdapter(supportFragmentManager);
        dashViewPagerId.adapter = sectionAdapter;
        mainTabs.setupWithViewPager(dashViewPagerId);
        mainTabs.setTabTextColors(Color.WHITE, Color.GRAY);



        if(intent.extras != null)
        {
            var username = intent.extras.get("name");
            Toast.makeText(this, "Hello: ${username.toString()}", Toast.LENGTH_LONG).show();
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if(item != null)
        {
            if(item.itemId == R.id.logoutId)
            {
                //log user out
                FirebaseAuth.getInstance().signOut();
                startActivity(Intent(this, MainActivity::class.java));
                finish();

            }

            if(item.itemId == R.id.settingsId)
            {
                //take user settingsActivity
                startActivity(Intent(this, SettingsActivity::class.java));
                //finish();
            }
            if(item.itemId == R.id.meetingsId)
            {
                startActivity(Intent(this, EventsActivity::class.java));
            }
        }

        return true;
    }

}
