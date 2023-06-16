package br.edu.mouralacerda.dm2y2023projetovida

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NovaAtividade : AppCompatActivity(), SensorEventListener, LocationListener {

    lateinit var txtDescricao: TextView
    lateinit var spinner: Spinner
    lateinit var edtNome: EditText

    var mSensorManager: SensorManager? = null
    var mAccelerometer: Sensor? = null
    var mLocationManager: LocationManager? = null
    val LOCATION_PERMISSION_REQUEST_CODE = 100

    private val id = 0
    private var accelX = 0.0
    private var accelY = 0.0
    private var accelZ = 0.0
    private var latitude = 0.0
    private var longitude = 0.0
    private var atleta = ""
    private var atividade = ""
    private var isRecording = false
    private var selectedOption: String = ""
    var movimentos: MutableList<Movimento> = mutableListOf()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecordActivityData()
                } else {
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_atividade)

        spinner = findViewById(R.id.spnAtividade)
        txtDescricao = findViewById(R.id.txtDescricao)
        edtNome = findViewById(R.id.edtNome)

        // ACELEROMETRO
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mSensorManager!!.flush(this)
        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME)

        // GPS
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        findViewById<Button>(R.id.btnIniciar).setOnClickListener {
            startRecordActivity()
        }

        findViewById<Button>(R.id.btnPausar).setOnClickListener{
            disableLocationUpdates()
        }

        findViewById<Button>(R.id.btnFinalizar).setOnClickListener{
            stopActivity()
        }

        val opcoes = listOf("Corrida", "Caminhada", "Pedalada")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var selectedOption = parent?.getItemAtPosition(position).toString()
                if (selectedOption == "Corrida"){
                    txtDescricao.text = "Essa é uma corrida"
                    atividade = "corrida"
                } else if (selectedOption == "Caminhada"){
                    txtDescricao.text = "Essa é uma caminhada"
                    atividade = "caminhada"
                } else{
                    txtDescricao.text = "Essa é uma pedalada"
                    atividade = "pedalada"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.home){
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        if (item.itemId == R.id.minhasAtividade){
            val intent = Intent(this, MinhasAtividades::class.java)
            startActivity(intent)
        }

        if (item.itemId == R.id.novaAtividade){
            val intent = Intent(this, NovaAtividade::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestLocationPermission() {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, fineLocationPermission) || ActivityCompat.shouldShowRequestPermissionRationale(this, coarseLocationPermission)) {
            Toast.makeText(this, "Permita o acesso à localização", Toast.LENGTH_LONG).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(fineLocationPermission, coarseLocationPermission), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun isGpsEnabled(): Boolean {
        return mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun startRecordActivity() {
        isRecording = true

        if (!isGpsEnabled()) {
            showGpsDisabledDialog()
        }

        requestLocationPermission()
    }

    fun startRecordActivityData() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && isRecording) {
            mLocationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this
            )
        }
    }

    fun stopActivity() {
        stopRecordActivityData()

        val gpxContent: String = createGpxDocument(movimentos, selectedOption)
        println(gpxContent)

        Log.d("itens adicionados", gpxContent)
    }

    fun stopRecordActivityData() {
        isRecording = false
        mLocationManager?.removeUpdates(this)
    }

    private fun disableLocationUpdates() {
        // Para a obtenção de atualizações de localização
        isRecording = false
        mLocationManager?.removeUpdates(this)
    }

    private fun showGpsDisabledDialog() {
        AlertDialog.Builder(this)
            .setMessage("O GPS está desligado. Por favor, habilite-o para usar esta função.")
            .setPositiveButton("Habilitar") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(settingsIntent)
            }
            .setNegativeButton("Cancelar") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                // Lidar com a ação de cancelamento, se necessário
                // Por exemplo, fechar a activity ou exibir outra mensagem para o usuário
                Toast.makeText(this, "A função requer GPS. O GPS não está habilitado.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            accelX = p0!!.values[0].toString().toDouble()
            accelY = p0!!.values[1].toString().toDouble()
            accelZ = p0!!.values[2].toString().toDouble()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onLocationChanged(p0: Location) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            latitude = p0.latitude.toString().toDouble()
            longitude = p0.longitude.toString().toDouble()
            atleta = edtNome.text.toString()

            var movimento = Movimento(
                id,
                getCurrentDateTime(),
                accelX,
                accelY,
                accelZ,
                latitude,
                longitude,
                atleta,
                atividade,
            )
            movimentos.add(movimento)

            CoroutineScope(Dispatchers.IO).launch {
                MovimentoDatabase.getInstance(this@NovaAtividade).MovimentoDataDao().save(movimento)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    fun getCurrentDateTime(): String {
        val currentTime = System.currentTimeMillis()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        formatter.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        val date = Date(currentTime)
        return formatter.format(date)
    }
}