package interfaz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.Conexion;
import main.Main;

public class Principal extends Application {

	private Stage stage;
	private double x, y;
	private Main main;
	private VBox panelConexiones;
	private int estado;
	private int nConexiones;
	private Label nConex;
	private StackPane centro;
	private StackPane centroProg;
	private StackPane centroPrin;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		main = new Main(20, this);
		main.start();
		estado = 0;
		nConexiones = 0;
		this.stage = stage;
		try {
			stage.getIcons().add(new Image(new FileInputStream("./data/Icono.png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		x = primaryScreenBounds.getWidth();
		y = primaryScreenBounds.getHeight();
		stage.setTitle("Suffragium - Jurado");
		stage.centerOnScreen();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				// main.finalizarVotos();
				System.exit(0);
			}
		});

		stage.show();
		load();

	}

	public void load() {
		estado = 1;
		stage.setWidth(x * 0.25);
		stage.setHeight(y * 0.5);
		stage.centerOnScreen();
		stage.setResizable(false);

		BorderPane border = new BorderPane();
		// Padding
		border.setPadding(new Insets(y * 0.05, 0, y * 0.025, 0));

		// Fondo
		BackgroundSize bSize = new BackgroundSize(0, 0, true, true, true, true);
		Background background = null;
		try {
			background = new Background(new BackgroundImage(new Image(new FileInputStream("./data/FondoCarga.jpg")),
					BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		border.setBackground(background);

		// Arriba
		ImageView logo = null;
		try {
			logo = new ImageView(new Image(new FileInputStream("./data/Logo.png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		logo.setFitWidth(x * 0.225);
		logo.setFitHeight(y * 0.07);
		border.setTop(logo);
		BorderPane.setAlignment(logo, Pos.BOTTOM_CENTER);

		// Centro

		ImageView gifCarga = null;
		try {
			gifCarga = new ImageView(new Image(new FileInputStream("./data/LoaderInicio.gif")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		gifCarga.setFitWidth(x * 0.2);
		gifCarga.setFitHeight(y * 0.15);
		border.setCenter(gifCarga);
		BorderPane.setAlignment(gifCarga, Pos.CENTER);

		// Abajo
		ImageView loading = null;
		try {
			loading = new ImageView(new Image(new FileInputStream("./data/Loading.png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		loading.setFitWidth(x * 0.15);
		border.setBottom(loading);
		BorderPane.setAlignment(loading, Pos.CENTER);

		Scene carga = new Scene(border);
		stage.setScene(carga);

		// Inicializa Espera
		ThreadEspera t = new ThreadEspera(this);
		t.start();
	}

	// Metodo que llama la inicializacion.
	public void threadEmpezar() {
		Platform.runLater(() -> empezar());
	}

	private void empezar() {
		estado = 2;
		stage.setWidth(x * 0.4);
		stage.setHeight(y * 0.5);
		stage.centerOnScreen();
		stage.setResizable(false);

		BorderPane border = new BorderPane();
		// Padding
		border.setPadding(new Insets(y * 0.05, 0, y * 0.02, 0));

		// Arriba
		ImageView logo = null;
		try {
			logo = new ImageView(new Image(new FileInputStream("./data/Logo.png")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		logo.setFitWidth(x * 0.225);
		logo.setFitHeight(y * 0.07);
		border.setTop(logo);
		BorderPane.setAlignment(logo, Pos.BOTTOM_CENTER);

		// Centro
		VBox vertical = new VBox();
		vertical.setPadding(new Insets(y * 0.03, x * 0.03, 0, x * 0.03));
		vertical.setSpacing(y * 0.02);

		// Direccion
		HBox direc = new HBox();
		direc.setAlignment(Pos.CENTER_LEFT);
		direc.setSpacing(x * 0.025);

		Label direcLbl = new Label("Direccion:");
		direcLbl.setFont(new Font(x / 60));
		direc.getChildren().add(direcLbl);

		Label infoLbl = new Label(main.getInfo());
		infoLbl.setFont(Font.font("Verdana", FontPosture.ITALIC, x / 50));
		direc.getChildren().add(infoLbl);

		// Clave

		HBox clave = new HBox();
		clave.setAlignment(Pos.CENTER_LEFT);
		clave.setSpacing(x * 0.025);

		Label claveLbl = new Label("Clave:");
		claveLbl.setFont(new Font(x / 60));
		clave.getChildren().add(claveLbl);

		Label keyLbl = new Label(main.getLlave());
		keyLbl.setFont(Font.font("Verdana", FontPosture.ITALIC, x / 50));
		clave.getChildren().add(keyLbl);

		// Numero de Conexiones
		VBox panelCon = new VBox();
		panelCon.setSpacing(x * 0.005);
		HBox panelInfo = new HBox();
		panelInfo.setAlignment(Pos.CENTER_LEFT);
		panelInfo.setSpacing(x * 0.005);
		Label conexLbl = new Label("Conexiones:");
		conexLbl.setFont(Font.font("Courier New", FontWeight.BOLD, x / 70));

		nConex = new Label();
		nConex.setFont(Font.font(x / 70));
		nConex.setText("" + nConexiones);
		panelInfo.getChildren().add(conexLbl);
		panelInfo.getChildren().add(nConex);
		// Panel al que se le agregan las conexiones.

		panelConexiones = new VBox();

		panelCon.getChildren().add(panelInfo);
		panelCon.getChildren().add(panelConexiones);
		// Boton de iniciar votaciones

		StackPane panelBoton = new StackPane();
		panelBoton.setPadding(new Insets(y * 0.025, 0, 0, 0));
		panelBoton.setAlignment(Pos.BOTTOM_CENTER);
		Button submit = new Button("Empezar Votaciones");
		submit.setFont(Font.font("Vederna", FontWeight.BOLD, x / 70));
		submit.setOnAction(e -> {
			iniciar();
		});
		panelBoton.getChildren().add(submit);

		vertical.getChildren().add(direc);
		vertical.getChildren().add(clave);
		vertical.getChildren().add(panelCon);
		vertical.getChildren().add(panelBoton);

		border.setCenter(vertical);
		BorderPane.setAlignment(vertical, Pos.CENTER);
		Scene llave = new Scene(border);
		stage.setScene(llave);
	}

	public void conexionRealizada(Conexion con) {
		if (estado == 2) {
			Platform.runLater(() -> agregarConexion(con));
		}
	}

	private void agregarConexion(Conexion con) {
		if (estado == 2) {
			nConexiones++;
			nConex.setText("" + nConexiones);

			HBox pan = new HBox();
			pan.setAlignment(Pos.CENTER);
			pan.setStyle("-fx-background-color: silver; -fx-background-radius: 10px;");
			pan.setMinSize(0.0, y * 0.04);

			Label puesto = new Label("Puesto: ");
			puesto.setFont(Font.font(x / 80));

			Label numero = new Label("" + nConexiones);
			numero.setFont(Font.font(x / 80));
			numero.setPadding(new Insets(0, x * 0.06, 0, 0));

			Label direccion = new Label("Direccion: ");
			direccion.setFont(Font.font(x / 80));
			;

			Label ip = new Label(con.getSock().getInetAddress().getHostAddress() + ":" + con.getSock().getPort());
			ip.setFont(Font.font(x / 80));

			pan.getChildren().add(puesto);
			pan.getChildren().add(numero);
			pan.getChildren().add(direccion);
			pan.getChildren().add(ip);

			panelConexiones.getChildren().add(pan);
			stage.setHeight(stage.getHeight() + y * 0.04);

		}
	}

	public void iniciar() {
		if (estado == 2) {
			if (nConexiones == -1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("No hay conexiones!");
				alert.show();
			} else {
				inicio();
			}
		}
	}

	public void inicio() {
		stage.setWidth(x * 0.75);
		stage.setHeight(y * 0.75);
		stage.centerOnScreen();
		stage.setResizable(true);

		BorderPane border = new BorderPane();
		border.setPadding(new Insets(y * 0.1, 0, y * 0.1, 0));

		Image fondo = new Image(
				"http://blogs.eltiempo.com/pineda-le-cuenta/wp-content/uploads/sites/722/2018/07/bandera-colombia.jpg");
		BackgroundSize bSize = new BackgroundSize(0, 0, true, true, true, true);
		Background bandera = new Background(new BackgroundImage(fondo, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize));
		border.setBackground(bandera);

		// Centro
		centro = new StackPane();
		centroPrin = new StackPane();
		centroPrin.getChildren().add(centro);

		centroProg = new StackPane();
		ProgressIndicator prog = new ProgressIndicator();
		prog.setScaleX(x * 0.0002);
		prog.setScaleY(x * 0.0002);
		prog.setStyle(" -fx-progress-color: black;");
		centroProg.getChildren().add(prog);

		border.setCenter(centroPrin);
		centro.setPadding(new Insets(y * 0.1, x * 0.25, y * 0.1, x * 0.25));
		BorderPane.setAlignment(centro, Pos.CENTER);

		// Abajo

//									Button ah = new Button("WHATTT");
//									ah.setOnAction(e -> {
//										agregarInfo("Andres Felipe Varon Maya", "1.126.808.447", "17-08-1996", "M", "Bogota", "Cundinamarca");
//									});
//									border.setBottom(ah);
//									BorderPane.setAlignment(ah, Pos.CENTER);

		BorderPane.setAlignment(prog, Pos.CENTER);

		Scene votando = new Scene(border);
		stage.setScene(votando);
		main.empezar();
	}

	public void confirmarIdentidad(String nombre, String cedula, String fecha, String genero, String municipio,
			String departamento) {
		Platform.runLater(() -> agregarInfo(nombre, cedula, fecha, genero, municipio, departamento));
	}

	private void agregarInfo(String nombre, String cedula, String fecha, String genero, String municipio,
			String departamento) {
		stage.getScene().getStylesheets().add(Principal.class.getResource("votando.css").toExternalForm());
		BorderPane nuevo = new BorderPane();
		nuevo.setPadding(new Insets(y * 0.02, 0, 0, 0));
		nuevo.setStyle("-fx-background-radius: 20;-fx-background-color: linear-gradient(#d2d1d1, silver);" + " ");
		// Arriba

		Label nombreLbl = new Label(nombre);
		nombreLbl.setFont(Font.font("Vederna", FontWeight.BOLD, x / 75));
		nuevo.setTop(nombreLbl);
		BorderPane.setAlignment(nombreLbl, Pos.BOTTOM_CENTER);

		// Centro

		VBox cent = new VBox();
		cent.setPadding(new Insets(y * 0.015, 0, 0, x * 0.02));
		cent.setSpacing(y * 0.015);
		BorderPane.setAlignment(cent, Pos.CENTER);

		// Cedula
		HBox ced = new HBox();
		ced.setAlignment(Pos.CENTER_LEFT);
		ced.setSpacing(x * 0.0025);
		Label infoCed = new Label("Cedula: ");
		infoCed.setFont(new Font(x / 90));

		Label cedu = new Label(cedula);
		cedu.setFont(Font.font(Font.getDefault().getFamily(), FontPosture.ITALIC, x / 90));

		ced.getChildren().add(infoCed);
		ced.getChildren().add(cedu);
		cent.getChildren().add(ced);

		// Fecha Nacimiento
		HBox fech = new HBox();
		fech.setAlignment(Pos.CENTER_LEFT);
		fech.setSpacing(x * 0.0025);
		Label infoFech = new Label("Fecha de nacimiento: ");
		infoFech.setFont(new Font(x / 90));

		Label fec = new Label(fecha);
		fec.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, x / 90));

		fech.getChildren().add(infoFech);
		fech.getChildren().add(fec);
		cent.getChildren().add(fech);

		// Genero

		HBox sex = new HBox();
		sex.setAlignment(Pos.CENTER_LEFT);
		sex.setSpacing(x * 0.0025);
		Label infoSex = new Label("Sexo: ");
		infoSex.setFont(new Font(x / 90));

		Label sexo = new Label(genero);
		sexo.setFont(Font.font("Verdana", FontWeight.BOLD, x / 90));

		sex.getChildren().add(infoSex);
		sex.getChildren().add(sexo);
		cent.getChildren().add(sex);

		nuevo.setCenter(cent);

		// Abajo
		HBox abajo = new HBox();
		abajo.setAlignment(Pos.CENTER);
		abajo.setPadding(new Insets(0, 0, x * 0.015, 0));
		abajo.setSpacing(x * 0.03);

		Button confirmar = new Button("Confirmar");
		confirmar.setId("confirmar");
		confirmar.setStyle("   -fx-font-size: " + x / 80 + "px;");

		Button cancelar = new Button("Cancelar");
		cancelar.setId("cancelar");
		cancelar.setStyle("   -fx-font-size: " + x / 80 + "px;");

		abajo.getChildren().add(confirmar);
		abajo.getChildren().add(cancelar);
		nuevo.setBottom(abajo);
		centro.getChildren().add(0, nuevo);

		Bounds boundsInScreen = nuevo.localToScreen(nuevo.getBoundsInLocal());

		confirmar.setOnAction(e -> {
			centroPrin.getChildren().add(centroProg);
			new ThreadVotar(this, cedula, nombre, nuevo, boundsInScreen, municipio, departamento).start();
		});

		cancelar.setOnAction(e -> {
			Path path = new Path();
			MoveTo moveTo = new MoveTo(x / 4 - boundsInScreen.getMaxX(), y / 4 - boundsInScreen.getMaxY() / 2);
			LineTo cubicCurveTo = new LineTo(x, y / 4 - boundsInScreen.getMaxY() / 2);
			path.getElements().add(moveTo);
			path.getElements().add(cubicCurveTo);
			PathTransition pathTransition = new PathTransition();
			pathTransition.setDuration(Duration.millis(2000));
			pathTransition.setNode(nuevo);
			pathTransition.setPath(path);
			pathTransition.setOrientation(PathTransition.OrientationType.NONE);
			pathTransition.setCycleCount(1);
			pathTransition.setAutoReverse(false);
			pathTransition.play();
			new ThreadBorrar(this, nuevo).start();
			main.noVotar(cedula);
		});

		Path path = new Path();
		MoveTo moveTo = new MoveTo(boundsInScreen.getMaxX() * -3.5, y / 4 - boundsInScreen.getMaxY() / 2);
		LineTo cubicCurveTo = new LineTo(x / 4 - boundsInScreen.getMaxX(), y / 4 - boundsInScreen.getMaxY() / 2);
		path.getElements().add(moveTo);
		path.getElements().add(cubicCurveTo);
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(2000));
		pathTransition.setNode(nuevo);
		pathTransition.setPath(path);
		pathTransition.setOrientation(PathTransition.OrientationType.NONE);
		pathTransition.setCycleCount(1);
		pathTransition.setAutoReverse(false);
		pathTransition.play();

	}

	public void votar(String cedula, String nombre, Node nuevo, Bounds boundsInScreen, String municipio,
			String departamento) {
		String puesto = main.votar(cedula, municipio, departamento);
		Platform.runLater(() -> {
			Path path = new Path();
			MoveTo moveTo = new MoveTo(x / 4 - boundsInScreen.getMaxX(), y / 4 - boundsInScreen.getMaxY() / 2);
			LineTo cubicCurveTo = new LineTo(x, y / 4 - boundsInScreen.getMaxY() / 2);
			path.getElements().add(moveTo);
			path.getElements().add(cubicCurveTo);
			PathTransition pathTransition = new PathTransition();
			pathTransition.setDuration(Duration.millis(2000));
			pathTransition.setNode(nuevo);
			pathTransition.setPath(path);
			pathTransition.setOrientation(PathTransition.OrientationType.NONE);
			pathTransition.setCycleCount(1);
			pathTransition.setAutoReverse(false);
			pathTransition.play();
			new ThreadBorrar(this, nuevo);
			centroPrin.getChildren().remove(centroProg);
			infoPuesto(puesto, nombre.split(" ")[0]);
		});

	}

	public void borrar(Node node) {
		Platform.runLater(() -> {
			centro.getChildren().remove(node);
		});
	}

	public void infoPuesto(String puesto, String nombre) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Confirmacion");
		alert.setHeaderText("Identidad Confirmada");
		alert.setContentText("El puesto " + puesto + " fue habilitado para " + nombre);
		alert.show();
	}
}
