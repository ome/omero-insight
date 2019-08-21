package org.openmicroscopy.shoola.agents.fsimporter.mde.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
/**
 * see also
 * http://stackoverflow.com/questions/25477999/jtextarea-filters-and-or-inputs-of-time-000000-java
 * 
 * @author kunis
 *
 */

public abstract class TimeField extends JPanel
{
	 private DocumentFilter minDocumentFilter;
	    private FocusListener hourFocusHandler;
	    private FocusListener minuteFocusHandler;

	    private JTextField hourField;
	    private JTextField minuteField;

	    private JPanel pnlFields;

	    private List<JComponent> focusOrder;

	    public TimeField() {

	        initUI();
	        installKeyBindings();

	    }

	    protected void initUI() {

	        setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        add(getTimeFieldsPanel(), gbc);

	    }

	    protected void installKeyBindings() {

	        JTextField hourField = getHourField();
	        installKeyBinding(
	                        hourField,
	                        JComponent.WHEN_FOCUSED,
	                        KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, KeyEvent.SHIFT_DOWN_MASK),
	                        "toMinuteField",
	                        new MoveFocusForward());
	        installKeyBinding(
	                        hourField,
	                        JComponent.WHEN_FOCUSED,
	                        KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
	                        "afterHourArrow",
	                        new MoveFieldFocusForward(hourField, true));
	        installKeyBinding(
	                        hourField,
	                        JComponent.WHEN_FOCUSED,
	                        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0),
	                        "afterHourKeyPad",
	                        new MoveFieldFocusForward(hourField, false));

	        JTextField minuteField = getMinuteField();
	        installKeyBinding(
	                        minuteField,
	                        JComponent.WHEN_FOCUSED,
	                        KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
	                        "beforeMinuteArrow",
	                        new MoveFieldFocusBackward(minuteField, true));
	        installKeyBinding(
	                        minuteField,
	                        JComponent.WHEN_FOCUSED,
	                        KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0),
	                        "beforeMinuteKeyPad",
	                        new MoveFieldFocusBackward(minuteField, false));

	    }

	    protected JPanel getTimeFieldsPanel() {

	        if (pnlFields == null) {

	            pnlFields = new JPanel(new GridBagLayout());
	            pnlFields.setBorder(new CompoundBorder(UIManager.getBorder("TextField.border"), new EmptyBorder(0, 2, 0, 2)));
	            pnlFields.setBackground(UIManager.getColor("TextField.background"));
	            GridBagConstraints gbc = new GridBagConstraints();
	            gbc.gridx = 0;
	            gbc.gridy = 0;
	            for (JComponent field : getTimeFields()) {
	                field.setBorder(null);
	                pnlFields.add(field, gbc);
	                gbc.gridx++;
	            }

	        }

	        return pnlFields;

	    }

	    protected JComponent[] getTimeFields() {
	        return new JComponent[]{
	            getHourField(),
	            new JLabel(":"),
	            getMinuteField()
	        };
	    }

	    protected List<JComponent> initFocusTransveralOrder() {
	        List<JComponent> focusOrder = new ArrayList<>(3);
	        focusOrder.add(getHourField());
	        focusOrder.add(getMinuteField());
	        return focusOrder;
	    }

	    protected List<JComponent> getFocusTraversalOrder() {
	        if (focusOrder == null) {
	            focusOrder = initFocusTransveralOrder();
	        }
	        return focusOrder;
	    }

	    protected int getHourFocusForwardLength() {
	        return 2;
	    }

	    protected int getMinuteFocusForwardLength() {
	        return 2;
	    }

	    protected JTextField createHourField() {
	        JTextField hourField = new JTextField(2);
	        ((AbstractDocument) hourField.getDocument()).setDocumentFilter(getHourDocumentFilter());
	        hourField.addFocusListener(getHourFocusHandler());
	        hourField.setHorizontalAlignment(JTextField.RIGHT);
	        hourField.getDocument().addDocumentListener(new MoveFocusForwardHandler(hourField, getHourFocusForwardLength()));
	        return hourField;
	    }

	    public JTextField getHourField() {
	        if (hourField == null) {
	            hourField = createHourField();
	        }
	        return hourField;
	    }

	    protected JTextField createMinuteField() {
	        JTextField minuteField = new JTextField(2);
	        ((AbstractDocument) minuteField.getDocument()).setDocumentFilter(getMinuteDocumentFilter());
	        minuteField.addFocusListener(getMinuteFocusHandler());
	        minuteField.setHorizontalAlignment(JTextField.RIGHT);
	        minuteField.getDocument().addDocumentListener(new MoveFocusForwardHandler(hourField, getMinuteFocusForwardLength()));
	        return minuteField;
	    }

	    public int getHour() throws NumberFormatException {
	        return getFieldValue(getHourField());
	    }

	    public int getMinute() throws NumberFormatException {
	        return getFieldValue(getMinuteField());
	    }

	    protected int getFieldValue(JTextComponent field) throws NumberFormatException {
	        return Integer.parseInt(field.getText());
	    }

	    public void setHour(int hour) {
	        setFieldValue(getHourField(), hour, 2);
	    }

	    public void setMinute(int minute) {
	        setFieldValue(getMinuteField(), minute, 2);
	    }

	    protected void setFieldValue(JTextComponent field, int value, int padding) {
	        String text = pad(value, padding);
	        field.setText(text);
	    }

	    public JTextField getMinuteField() {
	        if (minuteField == null) {
	            minuteField = createMinuteField();
	        }
	        return minuteField;
	    }

	    /**
	     * Returns the document filter used to filter the hour field
	     *
	     * @return
	     */
	    protected abstract DocumentFilter getHourDocumentFilter();

	    /**
	     * Returns the document filter user to filter the minute field
	     *
	     * @return
	     */
	    protected DocumentFilter getMinuteDocumentFilter() {
	        if (minDocumentFilter == null) {
	            minDocumentFilter = new MinuteDocumentFilter();
	        }
	        return minDocumentFilter;
	    }

	    /**
	     * Returns the focus listener used to monitor the hour field
	     *
	     * @return
	     */
	    protected FocusListener getHourFocusHandler() {
	        if (hourFocusHandler == null) {
	            hourFocusHandler = new HourFocusHandler();
	        }
	        return hourFocusHandler;
	    }

	    /**
	     * Used the focus listener used to monitor the minute field
	     *
	     * @return
	     */
	    protected FocusListener getMinuteFocusHandler() {
	        if (minuteFocusHandler == null) {
	            minuteFocusHandler = new MinuteFocusHandler();
	        }
	        return minuteFocusHandler;
	    }

	    public static String pad(long lValue, int iMinLength) {
	        return pad(Long.toString(lValue), 2);
	    }

	    public static String pad(int iValue, int iMinLength) {
	        return pad(Integer.toString(iValue), iMinLength);
	    }

	    public static String pad(String sValue, int iMinLength) {
	        StringBuilder sb = new StringBuilder(iMinLength);
	        sb.append(sValue);
	        while (sb.length() < iMinLength) {
	            sb.insert(0, "0");
	        }
	        return sb.toString();
	    }

	    protected void hourFieldLostFocus(FocusEvent evt) {
	        if (!evt.isTemporary()) {
	            String text = getHourField().getText();
	            if (text.length() < 2) {
	                text = pad(text, 2);
	                getHourField().setText(text);
	            }
	        }
	    }

	    protected void minuteFieldLostFocus(FocusEvent evt) {
	        if (!evt.isTemporary()) {
	            String text = getMinuteField().getText();
	            if (text.length() < 2) {
	                getMinuteField().setText(text + "0");
	            }
	        }
	    }

	    protected void installKeyBinding(JComponent field, int condition, KeyStroke keyStroke, String name, Action action) {
	        InputMap im = field.getInputMap(condition);
	        im.put(keyStroke, name);

	        ActionMap am = field.getActionMap();
	        am.put(name, action);
	    }

	    protected void moveFocusToNextField(JComponent parent) {
	        List<JComponent> order = getFocusTraversalOrder();
	        Component current = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
	        if (current == parent) {
	            int index = order.indexOf(current);
	            if (index != -1) {
	                if (index == order.size() - 1) {
	                    transferFocus();
	                } else {
	                    JComponent next = order.get(index + 1);
	                    next.requestFocusInWindow();
	                }
	            } else {
	                transferFocus();
	            }
	        }
	    }

	    protected void moveFocusToPreviousField(JComponent parent) {
	        List<JComponent> order = getFocusTraversalOrder();
	        Component current = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
	        if (current == parent) {
	            int index = order.indexOf(current);
	            if (index != -1) {
	                if (index == 0) {
	                    transferFocusBackward();
	                } else {
	                    JComponent previous = order.get(index - 1);
	                    previous.requestFocusInWindow();
	                }
	            } else {
	                transferFocusBackward();
	            }
	        }
	    }

	    protected abstract class AbstractFocusHandler extends FocusAdapter {

	        @Override
	        public void focusGained(FocusEvent e) {
	            if (e.getComponent() instanceof JTextComponent) {
	                JTextComponent field = (JTextComponent) e.getComponent();
	                field.selectAll();
	            }
	        }

	    }

	    /**
	     * Hour field focus handler. This watches for focus lost events a automatically pads the field with a leading "0" if the field is only 1 character in length
	     */
	    protected class HourFocusHandler extends AbstractFocusHandler {

	        @Override
	        public void focusLost(FocusEvent e) {
	            hourFieldLostFocus(e);
	        }
	    }

	    /**
	     * Minute field focus handler, watches for focus lost events and automatically adds a "0" to the end of the field if it is only 1 character in length
	     */
	    protected class MinuteFocusHandler extends AbstractFocusHandler {

	        @Override
	        public void focusLost(FocusEvent e) {
	            minuteFieldLostFocus(e);
	        }

	    }

	    protected class MoveFocusForwardHandler implements DocumentListener {

	        private JTextComponent parent;
	        private int maxLength;

	        public MoveFocusForwardHandler(JTextComponent parent, int maxLength) {
	            this.parent = parent;
	            this.maxLength = maxLength;
	        }

	        public int getMaxLength() {
	            return maxLength;
	        }

	        public JTextComponent getParent() {
	            return parent;
	        }

	        @Override
	        public void insertUpdate(DocumentEvent e) {
	            documentChanged(e);
	        }

	        @Override
	        public void changedUpdate(DocumentEvent e) {
	            documentChanged(e);
	        }

	        @Override
	        public void removeUpdate(DocumentEvent e) {
	            documentChanged(e);
	        }

	        protected void documentChanged(DocumentEvent e) {
	            if (getMaxLength() > 0) {
	                if (e.getDocument().getLength() >= getMaxLength()) {
	                    moveFocusToNextField(getParent());
	                }
	            }
	        }

	    }

	    /**
	     * The document filter used to filter the minute field.
	     */
	    protected class MinuteDocumentFilter extends DocumentFilter {

	        @Override
	        public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
	            super.insertString(fb, offset, text, attr);
	        }

	        @Override
	        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

	            try {
	                boolean isAcceptable = false;

	                // How long is the text been added
	                int strLength = text.length();
	                // Convert the value to an integer now and save us the hassel
	                int value = Integer.parseInt(text);

	                // If the length is only 1, probably a new character has been added
	                if (strLength == 1) {
	                    // The valid range of values we can accept
	                    int upperRange = 9;
	                    int lowerRange = 0;
	                    if (offset == 0) {
	                        // If we are at the first edit position, we can only accept values
	                        // from 0-5 (50 minutes that is)
	                        upperRange = 5;
	                    }

	                    // Is the value acceptable..
	                    if (value >= lowerRange && value <= upperRange) {
	                        isAcceptable = true;
	                    }
	                } else {
	          // Basically, we are going to trim the value down to at max 2 characters

	                    // Need to know at what offest...
	                    // 2 - offset..
	                    // offset == 0, length = 2 - offset = 2
	                    // offset == 1, length = 2 - offset = 1
	                    strLength = 2 - offset;
	                    String timeText = text.substring(offset, strLength);
	                    value = Integer.parseInt(timeText);
	                    if (value >= 0 && value <= 59) {
	                        // Pad out the value as required
	                        text = pad(value, 2);
	                        isAcceptable = true;
	                    }
	                }

	                if (isAcceptable) {
	                    super.replace(fb, offset, length, text, attrs);
	                    if (fb.getDocument().getLength() == 2) {
	                        moveFocusToNextField(getMinuteField());
	                    }
	                }

	            } catch (NumberFormatException exp) {
	            }
	        }
	    }

	    public class TimeFocusTraversalPolicy
	                    extends FocusTraversalPolicy {

	        private List<Component> order;

	        public TimeFocusTraversalPolicy(List<Component> order) {
	            this.order = new ArrayList<>(order.size());
	            this.order.addAll(order);
	        }

	        @Override
	        public Component getComponentAfter(Container focusCycleRoot,
	                        Component aComponent) {
	            int idx = (order.indexOf(aComponent) + 1) % order.size();
	            return order.get(idx);
	        }

	        @Override
	        public Component getComponentBefore(Container focusCycleRoot,
	                        Component aComponent) {
	            int idx = order.indexOf(aComponent) - 1;
	            if (idx < 0) {
	                idx = order.size() - 1;
	            }
	            return order.get(idx);
	        }

	        @Override
	        public Component getDefaultComponent(Container focusCycleRoot) {
	            return order.get(0);
	        }

	        @Override
	        public Component getLastComponent(Container focusCycleRoot) {
	            return order.get(order.size() - 1);
	        }

	        @Override
	        public Component getFirstComponent(Container focusCycleRoot) {
	            return order.get(0);
	        }
	    }

	    public class MoveFocusForward extends AbstractAction {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            moveFocusToNextField((JComponent) e.getSource());
	        }

	    }

	    public class MoveFocusBackward extends AbstractAction {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            moveFocusToPreviousField((JComponent) e.getSource());
	        }

	    }

	    public class MoveFieldFocusForward extends AbstractAction {

	        private JTextComponent field;
	        private final boolean ignoreNumLock;

	        public MoveFieldFocusForward(JTextComponent field, boolean ignoreNumLock) {
	            this.field = field;
	            this.ignoreNumLock = ignoreNumLock;
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            boolean numLockOff = false;
	            try {
	                // Get the state of the nums lock
	                numLockOff = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
	            } catch (Exception exp) {
	            }
	            if (ignoreNumLock || !numLockOff) {
	                if (field.getCaretPosition() >= field.getDocument().getLength()) {
	                    moveFocusToNextField((JComponent) e.getSource());
	                }
	            }
	        }

	    }

	    public class MoveFieldFocusBackward extends AbstractAction {

	        private JTextComponent field;
	        private final boolean ignoreNumLock;

	        public MoveFieldFocusBackward(JTextComponent field, boolean ignoreNumLock) {
	            this.field = field;
	            this.ignoreNumLock = ignoreNumLock;
	        }

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            boolean numLockOff = false;
	            try {
	                // Get the state of the nums lock
	                numLockOff = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK);
	            } catch (Exception exp) {
	            }
	            if ((ignoreNumLock || !numLockOff) && field.getCaretPosition() <= 1) {
	                moveFocusToPreviousField((JComponent) e.getSource());
	            }
	        }

	    }
}
