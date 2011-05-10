/*
 *  soapUI, copyright (C) 2004-2011 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.security.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.eviware.soapui.config.StrategyTypeConfig;
import com.eviware.soapui.impl.wsdl.actions.project.SimpleDialog;
import com.eviware.soapui.model.security.SecurityCheck;
import com.eviware.soapui.model.security.SecurityParametersTableModel;
import com.eviware.soapui.security.assertion.SecurityAssertionPanel;
import com.eviware.soapui.security.check.AbstractSecurityCheckWithProperties;
import com.eviware.soapui.security.ui.SecurityConfigurationDialogBuilder.Strategy;
import com.eviware.soapui.support.UISupport;
import com.eviware.x.form.XFormDialog;
import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormFieldListener;
import com.eviware.x.form.support.ADialogBuilder;
import com.eviware.x.form.support.XFormRadioGroup;
import com.eviware.x.impl.swing.JFormDialog;

public class SecurityConfigurationDialog extends SimpleDialog
{
	private final SecurityCheck securityCheck;
	private boolean result;

	public SecurityConfigurationDialog( SecurityCheck securityCheck )
	{
		super( securityCheck.getName(), securityCheck.getDescription(), securityCheck.getHelpURL() );

		this.securityCheck = securityCheck;
	}

	public SecurityCheck getSecurityCheck()
	{
		return securityCheck;
	}

	@Override
	protected Component buildContent()
	{
		JPanel mainPanel = UISupport.createEmptyPanel( 5, 5, 5, 5 );

		if( securityCheck instanceof AbstractSecurityCheckWithProperties )
		{
			JPanel topPanel = UISupport.createEmptyPanel( 0, 0, 10, 10 );
			topPanel.add( buildParametersTable(), BorderLayout.CENTER );

			JPanel p = UISupport.createEmptyPanel( 5, 5, 0, 10 );
			JLabel jLabel = new JLabel( "Parameters:" );
			jLabel.setPreferredSize( new Dimension( 60, 20 ) );
			p.add( jLabel, BorderLayout.NORTH );

			topPanel.add( p, BorderLayout.WEST );

			JComponent component = securityCheck.getComponent();
			if( component != null )
				topPanel.add( component, BorderLayout.SOUTH );

			mainPanel.add( topPanel, BorderLayout.NORTH );
		}
		else
		{
			JComponent component = securityCheck.getComponent();
			if( component != null )
			{
				JPanel topPanel = UISupport.createEmptyPanel( 0, 0, 10, 10 );
				topPanel.add( component, BorderLayout.SOUTH );
				mainPanel.add( topPanel, BorderLayout.NORTH );
			}
		}

		Dimension prefSize = mainPanel.getPreferredSize();
		int prefHeight = ( int )( prefSize.getHeight() + 170 );
		int prefWidth = ( int )Math.max( prefSize.getWidth(), 600 );

		mainPanel.setPreferredSize( new Dimension( prefWidth, prefHeight ) );

		mainPanel.add( buildTabs(), BorderLayout.CENTER );

		return mainPanel;
	}

	protected Component buildParametersTable()
	{
		SecurityCheckedParametersTablePanel parametersTable = new SecurityCheckedParametersTablePanel(
				new SecurityParametersTableModel(
						( ( AbstractSecurityCheckWithProperties )securityCheck ).getParameterHolder() ), securityCheck
						.getTestStep().getProperties(), ( AbstractSecurityCheckWithProperties )securityCheck );

		parametersTable.setPreferredSize( new Dimension( 400, 150 ) );
		return parametersTable;
	}

	protected Component buildTabs()
	{
		JTabbedPane tabs = new JTabbedPane();

		tabs.addTab( "Assertions", new SecurityAssertionPanel( securityCheck ) );

		tabs.addTab( "Strategy", buildStrategyTab() );

		JComponent advancedSettingsPanel = securityCheck.getAdvancedSettingsPanel();
		if( advancedSettingsPanel != null )
			tabs.addTab( "Advanced", new JScrollPane( advancedSettingsPanel ) );

		return tabs;
	}

	protected Component buildStrategyTab()
	{
		XFormDialog dialog = ADialogBuilder.buildDialog( SecurityConfigurationDialogBuilder.Strategy.class, null );

		XFormRadioGroup strategy = ( XFormRadioGroup )dialog.getFormField( Strategy.STRATEGY );
		final String[] strategyOptions = new String[] { "One by One", "All At Once" };
		strategy.setOptions( strategyOptions );

		if( securityCheck.getExecutionStrategy().getStrategy() == StrategyTypeConfig.NO_STRATEGY )
			strategy.setEnabled( false );
		else
		{
			if( securityCheck.getExecutionStrategy().getStrategy() == StrategyTypeConfig.ONE_BY_ONE )
				strategy.setValue( strategyOptions[0] );
			else
				strategy.setValue( strategyOptions[1] );
		}

		// default is ONE_BY_ONE
		if( securityCheck.getExecutionStrategy().getImmutable() )
		{
			strategy.setDisabled();
		}

		strategy.addFormFieldListener( new XFormFieldListener()
		{
			@Override
			public void valueChanged( XFormField sourceField, String newValue, String oldValue )
			{

				if( newValue.equals( strategyOptions[0] ) )
					securityCheck.getExecutionStrategy().setStrategy( StrategyTypeConfig.ONE_BY_ONE );
				else
					securityCheck.getExecutionStrategy().setStrategy( StrategyTypeConfig.ALL_AT_ONCE );

			}
		} );

		XFormField delay = dialog.getFormField( Strategy.DELAY );
		delay.setValue( String.valueOf( securityCheck.getExecutionStrategy().getDelay() ) );

		delay.addFormFieldListener( new XFormFieldListener()
		{

			@Override
			public void valueChanged( XFormField sourceField, String newValue, String oldValue )
			{
				try
				{
					Integer.valueOf( newValue );
					securityCheck.getExecutionStrategy().setDelay( Integer.valueOf( newValue ) );
				}
				catch( Exception e )
				{
					UISupport.showErrorMessage( "Delay value must be integer number" );
				}
			}
		} );
		XFormField applyToFailedTests = dialog.getFormField( Strategy.APPLY_TO_FAILED_STEPS );
		applyToFailedTests.setValue( String.valueOf( securityCheck.isApplyForFailedStep() ) );
		applyToFailedTests.addFormFieldListener( new XFormFieldListener()
		{

			@Override
			public void valueChanged( XFormField sourceField, String newValue, String oldValue )
			{
				securityCheck.setApplyForFailedTestStep( Boolean.parseBoolean( newValue ) );
			}
		} );

		return ( ( JFormDialog )dialog ).getPanel();
	}

	@Override
	protected boolean handleOk()
	{
		result = true;
		return true;
	}

	public boolean configure()
	{
		result = false;
		setVisible( true );
		return result;
	}

}