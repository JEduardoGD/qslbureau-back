package egd.fmre.qslbureau.capture.util;

import egd.fmre.qslbureau.capture.dto.imageutil.Dimensions;

public abstract class StaticsShipLabelUtil {
	private StaticsShipLabelUtil() {
		// no call
	}

	public static final String NORMAL_COLOR = "000000";
	public static final Dimensions LETTER_DIMENSION = new Dimensions(12240, 15840);
	// 12240 Twips = 12240/20 = 612 pt = 612/72 = 8.5"
	// 15840 Twips = 15840/20 = 792 pt = 792/72 = 11"
	public static final Dimensions QRCODE_DIMENSION = new Dimensions(100, 100);
	public static final String CONFIRM_URL = "https://buro.fmre.mx/confirm";

	public static final String HEADER = "QSL BURÓ | Federación Mexicana de Radioexperimentadores.";

	public static final String PARAGRAPH_1 = "Buen dia, estimado usuario.";
	public static final String PARAGRAPH_2 = "De parte del qsl buró de la FMRE recibe un saludo al "
			+ "tiempo que te hacemos llegar tus tarjetas QSL recibidas por el mismo.";
	public static final String PARAGRAPH_3 = "Si te es posible, por favor "
			+ "ayúdanos a confirmar que has recibido este paquete escaneando el siguiente código QR "
			+ "con tu smartphone o visitando la siguiente liga: %s donde igualmente, podrás dejar "
			+ "algún comentario pertinente si lo consideras necesario.";

	public static final String PARAGRAPH_4 = "Tu confirmación nos ayuda a mejorar el proceso y poder "
			+ "tener una referencia objetiva del funcionamiento del buró, los comentarios no son anónimos.";
}
