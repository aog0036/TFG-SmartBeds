package com.example.smartbeds.generalActivities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartbeds.R;
import com.example.smartbeds.session.Session;
import com.example.smartbeds.bedManagement.BedsManagementActivity;
import com.example.smartbeds.bedMonitoring.BedsActivity;
import com.example.smartbeds.userManagement.UserPassChangeActivity;
import com.example.smartbeds.userManagement.UsersManagementActivity;

/**
 * UI con el menú de administración.
 * @author Alicia Olivares Gil
 */
public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final Context context = this;

    private ProgressBar progressBar;
    private DrawerLayout drawer;
    private NavigationView navigation;

    /**
     * Cierra el menú de navegación o finaliza el activity.
     */
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.closeDrawers();
        }else {
            super.onBackPressed();
        }
    }

    /**
     * Oculta la barra de carga.
     */
    @Override
    protected void onResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Construye la UI y crea el menú de navegación.
     * @param savedInstanceState savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Session session = Session.getInstance();
        if (session.getToken() == null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        progressBar = findViewById(R.id.admin_progress);

        //Crea el menú de neavegación.
        createNavigationMenu();
    }

    /**
     * Listener para el botón que lleva a la pantalla de visualizar camas.
     * @param view Vista.
     */
    public void visualizarCamas(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(context, BedsActivity.class);
        startActivity(intent);
    }

    /**
     * Listener para el botón que lleva a la pantalla de gestionar camas.
     * @param view Vista.
     */
    public void gestionarCamas(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(context, BedsManagementActivity.class);
        startActivity(intent);
    }

    /**
     * Listener para el botón que lleva a la pantalla de gestionar usuarios.
     * @param view Vista.
     */
    public void gestionarUsuarios(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(context, UsersManagementActivity.class);
        startActivity(intent);
    }

    /**
     * Crea el menú de navegación.
     */
    private void createNavigationMenu() {
        Session session = Session.getInstance();
        drawer = findViewById(R.id.drawer_layout);
        navigation = findViewById(R.id.navigation_view);
        LinearLayout lLayout = (LinearLayout) navigation.getHeaderView(0).getRootView();
        RelativeLayout rLayout = (RelativeLayout) lLayout.getChildAt(0);

        TextView username = (TextView) rLayout.getChildAt(0);
        username.setText(session.getUsername());

        Menu menu = navigation.getMenu();


        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);

        navigation.setNavigationItemSelectedListener(this);
    }

    /**
     * Listener para el botón que muestra el menú de navegación.
     * @param view Vista.
     */
    public void showMenu(View view){
        drawer.openDrawer(Gravity.LEFT);
    }

    /**
     * Listener para la selección de una opción del menú de navegación.
     * @param menuItem Item seleccionado.
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Session session = Session.getInstance();
        Intent intent;
        switch ((String) menuItem.getTitle()) {
            case "Modificar Contraseña":
                intent = new Intent(context, UserPassChangeActivity.class);
                Bundle b = new Bundle();
                b.putString("username", session.getUsername());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case "Cerrar sesión":
                Session.resetSession();
                intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case "Acerca de":
                intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
