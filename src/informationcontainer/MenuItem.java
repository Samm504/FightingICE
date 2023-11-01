package informationcontainer;

/**
 * Class for handling menu screen items.
 */
public class MenuItem {

	/**
	 * Text of the menu item.
	 */
	private String string;

	/**
	 * X-coordinate of the menu item.
	 */
	private int coordinateX;

	/**
	 * Y-coordinate of the menu item.
	 */
	private int coordinateY;

	/**
	 * Number representing the cursor position.
	 */
	private int cursorPosition;

	/**
	 * Class constructor to create an instance of the MenuItem class. 
	 * Initializes the instance with the specified menu item text, 
	 * X and Y coordinates for displaying the menu item, 
	 * and a cursor position number for displaying the selection cursor.
	 *
	 * @param string
	 *            Text of the menu item
	 * @param coordinateX
	 *            X-coordinate of the menu item
	 * @param coordinateY
	 *            Y-coordinate of the menu item
	 * @param cursorPosition
	 *            Cursor position number
	 */
	public MenuItem(String string, int coordinateX, int coordinateY, int cursorPosition) {
		this.string = string;
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.cursorPosition = cursorPosition;
	}

	/**
	 * Get the text of the menu item.
	 *
	 * @return Text of the menu item
	 */
	public String getString() {
		return this.string;
	}

	/**
	 * Get the X-coordinate of the menu item.
	 *
	 * @return X-coordinate of the menu item
	 */
	public int getCoordinateX() {
		return this.coordinateX;
	}

	/**
	 * Get the Y-coordinate of the menu item.
	 *
	 * @return Y-coordinate of the menu item
	 */
	public int getCoordinateY() {
		return this.coordinateY;
	}

	/**
	 * Get the cursor position number of the menu item.
	 *
	 * @return Cursor position number of the menu item
	 */
	public int getCursorPosition() {
		return this.cursorPosition;
	}
}
