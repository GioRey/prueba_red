import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
// import 'package:wifi_iot/wifi_iot.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> with WidgetsBindingObserver {
  static const platform = MethodChannel('foreground_service');

  String? red = 'NO HAY RED';

  bool _isStarted = false;

  void startForegroundService() async {
    try {
      await platform.invokeMethod('startService');
      setState(() {
        _isStarted = true;
      });
      // await switchWiFi("Megacable_5G_8540", "HXbapQRb");
    } on PlatformException catch (e) {
      setState(() {
        _isStarted = false;
      });
      if (kDebugMode) {
        print("Error al iniciar el servicio: '${e.message}'.");
      }
    }
  }

  void stopForegroundService() async {
    try {
      await platform.invokeMethod('stopService');
      setState(() {
        _isStarted = false;
      });
      // await switchWiFi("Megacable_2.4G_8540", "HXbapQRb");
    } on PlatformException catch (e) {
      setState(() {
        _isStarted = true;
      });
      if (kDebugMode) {
        print("Error al detener el servicio: '${e.message}'.");
      }
    }
  }

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    WidgetsBinding.instance.handleRequestAppExit().then((_) => onAppExit());
    // WidgetsBinding.instance.addPostFrameCallback((_) async {
    //   red = await WiFiForIoTPlugin.getSSID();
    // });
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);

    /*if (state == AppLifecycleState.resumed) {
      // La aplicación está en primer plano
      startForegroundService();
      if (kDebugMode) {
        print("App en primer plano");
      }
    } else if (state == AppLifecycleState.inactive) {
      startForegroundService();
      if (kDebugMode) {
        print("App inactiva");
      }
    } else if (state == AppLifecycleState.paused) {
      startForegroundService();
      if (kDebugMode) {
        print("App en segundo plano");
      }
    } else if (state == AppLifecycleState.detached) {
      stopForegroundService();
      if (kDebugMode) {
        print("App cerrada");
      }
    }*/
  }

  // Detectar cuando la app se cierra manualmente
  void onAppExit() {
    stopForegroundService();
    print("App cerrada manualmente");
  }

  // /// Conectar a una red Wi-Fi con contraseña
  // Future<void> switchWiFi(String ssid, String password) async {
  //   bool isConnected = await WiFiForIoTPlugin.connect(
  //     ssid,
  //     password: password,
  //     security:
  //         NetworkSecurity.WPA, // Ajusta el tipo de seguridad si es necesario
  //   );

  //   if (isConnected) {
  //     red = ssid;
  //     print("Conectado a $ssid");
  //   } else {
  //     print("No se pudo conectar a $ssid");
  //   }
  // }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'EL SERVICIO ESTA INICIADO',
            ),
            Text(
              '$_isStarted',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
            Text(
              'RED: ${red ?? 'NO HAY RED'}',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          if (_isStarted) {
            if (kDebugMode) {
              print('SE DETUVO EL SERVICIO');
            }
            stopForegroundService();
          } else {
            if (kDebugMode) {
              print('SE INICIO EL SERVICIO');
            }
            startForegroundService();
          }
        },
        tooltip: 'INICIAR TERMINAR SERVICIO',
        child: const Icon(Icons.add),
      ),
    );
  }
}
