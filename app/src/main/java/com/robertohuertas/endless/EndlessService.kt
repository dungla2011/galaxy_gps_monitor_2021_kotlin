package com.robertohuertas.endless

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class EndlessService : Service(), LocationListener {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
//    private lateinit var locationRequest: LocationRequest
//
//    // globally declare LocationCallback
//    private lateinit var locationCallback: LocationCallback


    override fun onBind(intent: Intent): IBinder? {
        log("location Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            log("using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> log("This should never happen. No action in the received intent")
            }
        } else {
            log(
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        log("The service has been created".toUpperCase())
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("The service has been destroyed".toUpperCase())
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        };
        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
    }
    
    private fun startService() {

        if (isServiceStarted) return

        log("Starting the galaxy service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }



        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    pingFakeServer()
                }
                delay(3 * 60 * 1000)
            }
            log("End of the loop for the service")
        }
    }

    private fun stopService() {
        log("Stopping the galaxy service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

//    private fun getLocation() {
//
//        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
//    }


    private fun pingFakeServer() {

//        getLocationUpdates()


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


//        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ")
//        val gmtTime = df.format(Date())

        val deviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

//        val json =
//            """
//                {
//                    "deviceId": "$deviceId",
//                    "createdAt": "$gmtTime"
//                }
//            """
        try {
            var locationOk = getLastKnownLocation(this);
//            "https://galaxycloud.vn/train/firebase/send-firebase-to-app-background-android-ok1.php?send_long_lat=test002".httpGet().response {
//                    request, response, result ->
//                //response handling
//            }
            val deviceName = Build.MODEL // returns model name

            var long1 = locationOk?.longitude;
            var lat1 = locationOk?.latitude;

            log("[location send get response bytes] $deviceName#$long1#$lat1")

            Fuel.get("https://galaxycloud.vn/tool/gps/info.php?send_info=device_send_$deviceName:$deviceId,lat-long:$lat1,$long1")
                .response { _, _, result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        log("[location response bytes] ${String(bytes)}")
                    } else {
                        log("[location response error] ${error?.message}")
                    }
                }
        } catch (e: Exception) {
            log("Error making the request: ${e.message}")
        }
    }

    /**
     * call this method in onCreate
     * onLocationResult call when location is changed
     */
//    private fun getLocationUpdates()
//    {
//
////        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
//
//        locationRequest = LocationRequest()
//        locationRequest.interval = 50000
//        locationRequest.fastestInterval = 50000
//        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                locationResult ?: return
//
//                log(" location 1")
//                if (locationResult.locations.isNotEmpty()) {
//                    // get latest location
//                    val location =
//                        locationResult.lastLocation
//                    log(" location 2")
//                    // use your location object
//                    // get latitude , longitude and other info from this
//                }
//
//            }
//        }
//    }
    /**
     * call this method for receive location
     * get location and give callback when successfully retrieve
     * function itself check location permission before access related methods
     *
     */
    fun getLastKnownLocation(context: Context) : Location? {
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        var location: Location? = null
        for (i in providers.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            location= locationManager.getLastKnownLocation(providers[i])
            if (location != null)
                break
        }
        val gps = DoubleArray(2)
        if (location != null) {
            gps[0] = location.getLatitude()
            gps[1] = location.getLongitude()
            log("location gpsLat: " + gps[0].toString())
            log("location gpsLong: " + gps[1].toString())

        }

        return location;

    }

    private fun createNotification(): Notification {
        val notificationChannelId = "Galaxy SERVICE CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                "Galaxy Service notifications channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Galaxy Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("Galaxy Service")
            .setContentText("This is your favorite galaxy service working")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

    override fun onLocationChanged(location: Location) {
//        tvGpsLocation = findViewById(R.id.textView)
//        tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude

    }
}
