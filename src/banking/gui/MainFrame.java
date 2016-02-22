package banking.gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import banking.primitive.core.Account;
import banking.primitive.core.AccountServer;
import banking.primitive.core.AccountServerFactory;

@SuppressWarnings("serial")
class MainFrame extends JFrame {
    final static String TYPE_LABEL = "TypeLabel";
    final static String NAME_LABEL = "NameLabel";
    final static String BALANCE_LABEL= "BalanceLabel";
    final static String NEW_ACCOUNT = "New Account";
    final static String SAVINGS = "Savings";
    final static String CHECKING = "Checking";
    final static String DEPOSIT = "Deposit";
    final static String WITHDRAW = "Withdraw";
    final static String SAVE_ACCOUNTS = "Save Accounts";
    final static String LIST_ACCOUNTS = "List Accounts";
    final static String ALL_ACCOUNTS = "All Accounts";
    final static String ACCOUNT_CREATED_SUCCESSFULY = "Account created successfully";
    final static String ACCOUNT_NOT_CREATED = "Account not created!";
    final static String ACCOUNTS_SAVED = "Accounts saved";
    final static String ERROR_SAVING_ACCOUNTS = "Error saving accounts";
    final static String DEPOSIT_SUCCESSFUL = "Deposit successful";
    final static String DEPOSIT_UNSUCCESFUL = "Deposit unsuccessful";
    final static String WITHDRAWAL_SUCCESSFUL = "Withdrawal successful";
    final static String WITHDRAWAL_UNSUCCESSFUL= "Withdrawal unsuccessful";
	AccountServer	myServer;
	Properties		props;
	JLabel			typeLabel;
	JLabel			nameLabel;
	JLabel			balanceLabel;
	JComboBox		typeOptions;
	JTextField		nameField;
	JTextField		balanceField;
	JButton 		depositButton;
	JButton 		withdrawButton;
	JButton			newAccountButton;
	JButton			displayAccountsButton;
	JButton			displayODAccountsButton;

	public MainFrame(String propertyFile) throws IOException {

		//** initialize myServer
		myServer = AccountServerFactory.getMe().lookup();

		props = new Properties();

		FileInputStream fis = null;
		try {
			fis =  new FileInputStream(propertyFile);
			props.load(fis);
			fis.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw ioe;
		}
		constructForm();
	}


	private void constructForm() {
		//*** Make these read from properties
		typeLabel		= new JLabel(props.getProperty(TYPE_LABEL));
		nameLabel		= new JLabel(props.getProperty(NAME_LABEL));
		balanceLabel	= new JLabel(props.getProperty(BALANCE_LABEL));

		String[] accountTypes = {SAVINGS, CHECKING};
		typeOptions = new JComboBox(accountTypes);
		nameField = new JTextField(20);
		balanceField = new JTextField(20);

		newAccountButton = new JButton(NEW_ACCOUNT);
		JButton depositButton = new JButton(DEPOSIT);
		JButton withdrawButton = new JButton(WITHDRAW);
		JButton saveButton = new JButton(SAVE_ACCOUNTS);
		displayAccountsButton = new JButton(LIST_ACCOUNTS);
		JButton displayAllAccountsButton = new JButton(ALL_ACCOUNTS);

		this.addWindowListener(new FrameHandler());
		newAccountButton.addActionListener(new NewAccountHandler());
		displayAccountsButton.addActionListener(new DisplayHandler());
		displayAllAccountsButton.addActionListener(new DisplayHandler());
		depositButton.addActionListener(new DepositHandler());
		withdrawButton.addActionListener(new WithdrawHandler());
		saveButton.addActionListener(new SaveAccountsHandler());

		Container pane = getContentPane();
		pane.setLayout(new FlowLayout());

		JPanel panel1 = new JPanel();
		panel1.add(typeLabel);
		panel1.add(typeOptions);

		JPanel panel2 = new JPanel();
		panel2.add(displayAccountsButton);
		panel2.add(displayAllAccountsButton);
		panel2.add(saveButton);

		JPanel panel3 = new JPanel();
		panel3.add(nameLabel);
		panel3.add(nameField);

		JPanel panel4 = new JPanel();
		panel4.add(balanceLabel);
		panel4.add(balanceField);

		JPanel panel5 = new JPanel();
		panel5.add(newAccountButton);
		panel5.add(depositButton);
		panel5.add(withdrawButton);

		pane.add(panel1);
		pane.add(panel2);
		pane.add(panel3);
		pane.add(panel4);
		pane.add(panel5);

		setSize(400, 250);
	}

	class DisplayHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			List<Account> accounts = null;
			if (e.getSource() == displayAccountsButton) {
				accounts = myServer.getActiveAccounts();
			} else {
				accounts = myServer.getAllAccounts();
			}
			StringBuffer sb = new StringBuffer();
			Account thisAcct = null;
			for (Iterator<Account> li = accounts.iterator(); li.hasNext();) {
				thisAcct = (Account)li.next();
				sb.append(thisAcct.toString()+"\n");
			}

			JOptionPane.showMessageDialog(null, sb.toString());
		}
	}

	// Complete a handler for new account button
	class NewAccountHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String type = typeOptions.getSelectedItem().toString();
			String name = nameField.getText();
			String balance = balanceField.getText();

			if (myServer.newAccount(type, name, Float.parseFloat(balance))) {
				JOptionPane.showMessageDialog(null, ACCOUNT_CREATED_SUCCESSFULY);
			} else {
				JOptionPane.showMessageDialog(null, ACCOUNT_NOT_CREATED);
			}
		}
	}

	// Complete a handler for new account button
	class SaveAccountsHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				myServer.saveAccounts();
				JOptionPane.showMessageDialog(null, ACCOUNTS_SAVED);
			} catch (IOException exc) {
				JOptionPane.showMessageDialog(null, ERROR_SAVING_ACCOUNTS);
			}
		}
	}

	// Complete a handler for deposit button
	class DepositHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = nameField.getText();
			String balance = balanceField.getText();
			Account acc = myServer.getAccount(name);
			if (acc != null && acc.deposit(Float.parseFloat(balance))) {
				JOptionPane.showMessageDialog(null, DEPOSIT_SUCCESSFUL);
			} else {
				JOptionPane.showMessageDialog(null, DEPOSIT_UNSUCCESFUL);
			}
		}
	}
	// Complete a handler for deposit button
	class WithdrawHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String name = nameField.getText();
			String balance = balanceField.getText();
			Account acc = myServer.getAccount(name);
			if (acc != null && acc.withdraw(Float.parseFloat(balance))) {
				JOptionPane.showMessageDialog(null, WITHDRAWAL_SUCCESSFUL);
			} else {
				JOptionPane.showMessageDialog(null, WITHDRAWAL_UNSUCCESSFUL);
			}
		}
	}

	//** Complete a handler for the Frame that terminates
	//** (System.exit(1)) on windowClosing event

	static class FrameHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {

			System.exit(0);
		}
	}
}
