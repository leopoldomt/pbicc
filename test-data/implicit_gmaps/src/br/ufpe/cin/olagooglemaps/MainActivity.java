package br.ufpe.cin.olagooglemaps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private final String TAG_LOGCAT = "OlaGoogleMaps";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG_LOGCAT, "Activity prestes a ser criada.");
		setContentView(R.layout.activity_main);

		// Fazendo o binding com os elementos do layout
		final EditText endereco = (EditText) findViewById(R.id.endereco);
		final Button botao = (Button) findViewById(R.id.botao);

		// Definindo um listener para eventos de clique no botao
		botao.setOnClickListener(new OnClickListener() {
			// este metodo é disparado quando clicamos no botão
			public void onClick(View v) {
				try {
					// obtem texto digitado
					String enderecoGMaps = endereco.getText().toString();
					// processa texto para envio ao Google Maps
					enderecoGMaps = enderecoGMaps.replace(' ', '+');

					// cria objeto Intent (implicito) para iniciar Google Maps
					Intent i = new Intent();
					// definindo action - VIEW (visualizar mapa)
					i.setAction(android.content.Intent.ACTION_VIEW);
					// criando Uri a ser passada para aplicacao de mapas
					Uri dados = Uri.parse("geo:0,0?q=" + enderecoGMaps);
					// definindo dados do intent
					i.setData(dados);

					// Dispara uma chamada para o sistema, que pesquisa por alguma
					// Activity que esteja apta a tratar a mensagem
					// neste caso, o google maps
					startActivity(i);

				} catch (Exception e) {
					// registrar mensagens de erro no LogCat 
					Log.e(TAG_LOGCAT, e.toString());
				}
			}
		});
		/**/

	}

	// os metodos abaixo sinalizam mudancas de estado no ciclo de vida da Activity 
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG_LOGCAT, "Activity visivel e prestes a ser iniciada.");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG_LOGCAT, "Activity visivel e prestes a ser reiniciada.");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG_LOGCAT, "Activity visivel e em primeiro plano (resumed)");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG_LOGCAT,
				"Outra Activity em primeiro plano, esta activity esta prestes a ser pausada");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG_LOGCAT, "Activity nao esta mais visivel (stopped)");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG_LOGCAT, "Activity prestes a ser destruida.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
