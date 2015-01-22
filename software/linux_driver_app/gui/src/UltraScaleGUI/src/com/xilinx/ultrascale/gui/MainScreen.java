package com.xilinx.ultrascale.gui;

import com.xilinx.ultrascale.jni.DMAStats;
import com.xilinx.ultrascale.jni.DriverInfo;
import com.xilinx.ultrascale.jni.EndPointInfo;
import com.xilinx.ultrascale.jni.EngState;
import com.xilinx.ultrascale.jni.PowerStats;
import com.xilinx.ultrascale.jni.TRNStats;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartPanel;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mobigesture
 */
public class MainScreen extends javax.swing.JFrame {

    LandingPage lp;
    private Timer timer;
    private Timer powerTimer;
    private TableModel tblModel1;
    EndPointInfo epInfo;

    /**
     * Creates new form MainScreen
     */
    private MainScreen() {
        initComponents();
        this.setLocationRelativeTo(null);
        ledicons[0] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/0.png"));
        ledicons[1] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/1.png"));
        ledicons[2] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/2.png"));
        ledicons[3] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/3.png"));
        ledicons[4] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/4.png"));
        ledicons[5] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/5.png"));
        ledicons[6] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/6.png"));
        ledicons[7] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/7.png"));
        ledicons[8] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/8.png"));
        ledicons[9] = new ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/9.png"));
    }

    public void configureScreen(int mode, LandingPage lp) {
        this.lp = lp;
        testStarted = false;
        if (Develop.production == 1) {
            di = null;
            di = new DriverInfo();
            di.init(mode);
            int ret = di.get_PCIstate();
            ret = di.getBarInfo();

            // fill pcie data
            if (mode != 6) { //ie., not a control plane
                pciemodel = new MyTableModel(di.getPCIInfo().getPCIData(), pcieColumnNames);
                pcieSysmontable.setModel(pciemodel);

                hostCredits = new MyTableModel(di.getPCIInfo().getHostedData(), pcieColumnNames);
                hostsysmontable.setModel(hostCredits);
            } else {
                pciemodel = new MyTableModel(di.getPCIInfo().getPCIDataForCP(), pcieColumnNames);
                pcieSysmontable.setModel(pciemodel);

                hostCredits = new MyTableModel(di.getPCIInfo().getHostedDataForCP(), pcieColumnNames);
                hostsysmontable.setModel(hostCredits);
                // changing the title of the panel of host credits.

            }
            testMode = DriverInfo.CHECKER;
            // initialize max packet size
            //ret = di.get_EngineState();
            //EngState[] engData = di.getEngState();
            epInfo = di.getEndPointInfo();
        }
//            maxSize = engData[0].MaxPktSize;
//            sizeTextField.setText(String.valueOf(maxSize));
        switch (mode) {
            // change heading and leds here.
            case 0://acc_perf_gen_check
                // No change in heading
                //hide all leds.
                ddricon.setVisible(false);
                DDR4label.setVisible(false);
                phy0icon.setVisible(false);
                phy0label.setVisible(false);
                phy1icon.setVisible(false);
                phty1label.setVisible(false);
                headinglable.setText("PCIe Based Accelerator : Performance Mode (GEN/CHK)");
                tblModel = new MyTableModel(dummydata, dmaColumnNames0);

                break;
            case 1://acc_perf_gen_check_ddr
                //hide all leds.
//                jLabel1.setVisible(false);
//                jLabel2.setVisible(false);
                phy0icon.setVisible(false);
                phy0label.setVisible(false);
                phy1icon.setVisible(false);
                phty1label.setVisible(false);
                headinglable.setText("PCIe Based Accelerator : Performance Mode (GEN/CHK with card DDR)");
                break;
            case 2://acc_application
                //hide all leds.

                phy0icon.setVisible(false);
                phy0label.setVisible(false);
                phy1icon.setVisible(false);
                phty1label.setVisible(false);
                headinglable.setText("PCIe Based Accelerator : Application Mode");
                break;
            case 3://ethernet_perf_raw
                ddricon.setVisible(false);
                DDR4label.setVisible(false);
                jCheckBox2.setEnabled(false);
                jCheckBox3.setEnabled(false);
                jCheckBox5.setEnabled(false);
                jCheckBox6.setEnabled(false);
                headinglable.setText("Ethernet AXI-4 Stream : Performance mode (Raw Ethernet)");
                break;
            case 7://ethernet_perf_gen_check
                ddricon.setVisible(false);
                DDR4label.setVisible(false);
                phy0icon.setVisible(false);
                phy0label.setVisible(false);
                phy1icon.setVisible(false);
                phty1label.setVisible(false);
                headinglable.setText("Ethernet AXI-4 Stream : Performance mode (GEN/CHK)");
                break;
            case 4://ethernet_app
                headinglable.setText("Ethernet AXI-4 Stream : Application Mode");
                // disabling the controls.
                ddricon.setVisible(false);
                DDR4label.setVisible(false);
                jCheckBox1.setEnabled(false);
                jCheckBox2.setEnabled(false);
                jCheckBox3.setEnabled(false);
                jCheckBox4.setEnabled(false);
                jCheckBox5.setEnabled(false);
                jCheckBox6.setEnabled(false);
                jButton2.setEnabled(false);
                jButton3.setEnabled(false);

                break;
            case 6://control_plane
                // Set combobox values
                barComboBoxTop.removeAllItems();
                barComboBoxTop1.removeAllItems();
                barComboBoxbottom.removeAllItems();
                if (epInfo.designMode == 0) {
                    barComboBoxTop.addItem("Bar2");
                    barComboBoxTop1.addItem("Bar2");
                    barComboBoxbottom.addItem("Bar2");
                } else if (epInfo.designMode == 1) {
                    barComboBoxTop.addItem("Bar4");
                    barComboBoxTop1.addItem("Bar4");
                    barComboBoxbottom.addItem("Bar4");
                } else if (epInfo.designMode == 2) {
                    barComboBoxTop.addItem("Bar2");
                    barComboBoxTop1.addItem("Bar2");
                    barComboBoxbottom.addItem("Bar2");
                    barComboBoxTop.addItem("Bar4");
                    barComboBoxTop1.addItem("Bar4");
                    barComboBoxbottom.addItem("Bar4");
                }
                ddricon.setVisible(false);
                DDR4label.setVisible(false);
                phy0icon.setVisible(false);
                phy0label.setVisible(false);
                phy1icon.setVisible(false);
                phty1label.setVisible(false);
                tabbedPanel.remove(PerformancePlotTab);
                tabbedPanel.remove(statusPanel);
                headinglable.setText("Control Plane");
                this.setSize(this.getSize().width + 40, 610);

                jPanel1.setPreferredSize(new Dimension(279, 600));//
                jPanel1.setSize(279, 600);

                jPanel1.add(PcieEndStatuspanel);
                jPanel1.add(hostCreditsPanel);
                // pcieSysmontable.setPreferredSize(new Dimension(250, 300));
                pcieSysmontable.getTableHeader().setReorderingAllowed(false);
                pcieSysmontable.getColumnModel().getColumn(1).setPreferredWidth(10);
                PcieEndStatuspanel.setSize(jPanel1.getSize().width, jPanel1.getSize().height - 365);
                PcieEndStatuspanel.setLocation(new Point(0, 0));

                hostCreditsPanel.setSize(jPanel1.getSize().width, jPanel1.getSize().height - 340);
                hostCreditsPanel.setLocation(new Point(0, 250));

                ((javax.swing.border.TitledBorder) hostCreditsPanel.getBorder()).setTitle("Endpoint BAR Information");
                jPanel1.repaint();
                jPanel1.revalidate();
//                PcieEndStatuspanel.repaint();
//                 PcieEndStatuspanel.revalidate();
                //TabelModel.getColoum(1).setsize();
                PowerPanel.setPreferredSize(new Dimension(310, 445));
                PowerPanel.revalidate();
                PowerPanel.repaint();
                /*MyTableModel tblModel = new MyTableModel(dummydata, pcieEndptClm);
                 pcieSysmontable.setModel(tblModel);
                 tblModel.setData(dataForPCIEDummy, dmaColumnNames0);
                 tblModel.fireTableDataChanged();*/
                MyTableModel tblModel1 = new MyTableModel(dummydata, hostPcie);
                hostsysmontable.setModel(tblModel1);
                tblModel1.setData(epInfo.getBarStats(), hostPcie);
                hostsysmontable.getColumnModel().getColumn(0).setPreferredWidth(5);
                hostsysmontable.getTableHeader().setReorderingAllowed(false);

                tblModel1.fireTableDataChanged();
                MyCellRenderer renderer = new MyCellRenderer();
                barDumpModel = new MyTableModel(bardumpDummy, bardumpNames);
                bardump.setModel(barDumpModel);
                bardump.getColumnModel().getColumn(0).setCellRenderer(renderer);
                bardump.getTableHeader().setReorderingAllowed(false);
                tabbedPanel.setLayout(new CardLayout());

                break;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        VideoPanel = new javax.swing.JPanel();
        topVidpanel = new javax.swing.JPanel();
        bottomVidpanel = new javax.swing.JPanel();
        DataPathPanel = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        ReadWritePanel = new javax.swing.JPanel();
        ReadPanel = new javax.swing.JPanel();
        offsetTextField1 = new javax.swing.JTextField();
        dataTextfield1 = new javax.swing.JTextField();
        executeRWButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        WritePanel = new javax.swing.JPanel();
        offsetTextField = new javax.swing.JTextField();
        dataTextfield = new javax.swing.JTextField();
        executeRWButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        hexdumppanel = new javax.swing.JPanel();
        executeBarButton = new javax.swing.JButton();
        AddressTextField = new javax.swing.JTextField();
        sizeControlTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        bardump = new javax.swing.JTable();
        jLabel14 = new javax.swing.JLabel();
        DataPathPanelForOneDP = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        loopBackCheckbox = new javax.swing.JCheckBox();
        CheckerChcekBox = new javax.swing.JCheckBox();
        GeneratorCheckbox = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        sizeTextField = new javax.swing.JTextField();
        jbuttonEngStart = new javax.swing.JButton();
        jPanel26 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        BlockDiagramPanel = new javax.swing.JPanel();
        blockdiagramlbl = new javax.swing.JLabel();
        barsControlPlane = new javax.swing.JPanel();
        barComboBoxbottom = new javax.swing.JComboBox();
        barComboBoxTop = new javax.swing.JComboBox();
        barComboBoxTop1 = new javax.swing.JComboBox();
        DyPanel = new javax.swing.JPanel();
        ControlPanel = new javax.swing.JPanel();
        logscrollpanel = new javax.swing.JScrollPane();
        logArea = new javax.swing.JTextArea();
        ledPanel = new javax.swing.JPanel();
        ddrpanel = new javax.swing.JPanel();
        ddricon = new javax.swing.JLabel();
        DDR4label = new javax.swing.JLabel();
        phy0panel = new javax.swing.JPanel();
        phy0icon = new javax.swing.JLabel();
        phy0label = new javax.swing.JLabel();
        phy1panel = new javax.swing.JPanel();
        phy1icon = new javax.swing.JLabel();
        phty1label = new javax.swing.JLabel();
        tabpanel = new javax.swing.JPanel();
        tabbedPanel = new javax.swing.JTabbedPane();
        sysmonpanel = new javax.swing.JPanel();
        tempholdPanel = new javax.swing.JPanel();
        tempvaluePanel = new javax.swing.JPanel();
        TempMeasureLabel = new javax.swing.JLabel();
        MinorTempLabel = new javax.swing.JLabel();
        MajorTempLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        PowerPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        PerformancePlotTab = new javax.swing.JPanel();
        topChartperfpanel = new javax.swing.JPanel();
        bottomChartperfpanel = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        PcieEndStatuspanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pcieSysmontable = new javax.swing.JTable();
        hostCreditsPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        hostsysmontable = new javax.swing.JTable();
        HeadingPanel = new javax.swing.JPanel();
        headinglable = new javax.swing.JLabel();
        blockdiagrambutton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        VideoPanel.setPreferredSize(new java.awt.Dimension(364, 404));

        topVidpanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout topVidpanelLayout = new javax.swing.GroupLayout(topVidpanel);
        topVidpanel.setLayout(topVidpanelLayout);
        topVidpanelLayout.setHorizontalGroup(
            topVidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        topVidpanelLayout.setVerticalGroup(
            topVidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 189, Short.MAX_VALUE)
        );

        bottomVidpanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout bottomVidpanelLayout = new javax.swing.GroupLayout(bottomVidpanel);
        bottomVidpanel.setLayout(bottomVidpanelLayout);
        bottomVidpanelLayout.setHorizontalGroup(
            bottomVidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 340, Short.MAX_VALUE)
        );
        bottomVidpanelLayout.setVerticalGroup(
            bottomVidpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 167, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout VideoPanelLayout = new javax.swing.GroupLayout(VideoPanel);
        VideoPanel.setLayout(VideoPanelLayout);
        VideoPanelLayout.setHorizontalGroup(
            VideoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VideoPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(VideoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bottomVidpanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(topVidpanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        VideoPanelLayout.setVerticalGroup(
            VideoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VideoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topVidpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomVidpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        DataPathPanel.setPreferredSize(new java.awt.Dimension(364, 404));

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Troughput(Gbps)", "0.000", "0.000"},
                {"BD Errors", "0", "0"},
                {"SW BDs", "1999", "1999"}
            },
            new String [] {
                "Parameters", "Transmit(S2C0)", "Receive(S2C0)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable3);

        jLabel8.setText("Data Path-1:");

        jCheckBox1.setText("Loopback");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("HW Checker");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("HW Generator");

        jLabel9.setText("Packet Size (bytes):");

        jTextField1.setText("32768");

        jButton2.setText("Start");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(jCheckBox3)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jCheckBox2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(1, 1, 1))))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Troughput(Gbps)", "0.000", "0.000"},
                {"BD Errors", "0", "0"},
                {"SW BDs", "1999", "1999"}
            },
            new String [] {
                "Parameters", "Transmit(S2C0)", "Receive(S2C0)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable4);

        jLabel10.setText("Data Path-0:");

        jCheckBox4.setText("Loopback");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        jCheckBox5.setText("HW Checker");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        jCheckBox6.setText("HW Generator");

        jLabel11.setText("Packet Size (bytes):");

        jTextField2.setText("32768");

        jButton3.setText("Start");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox4)
                            .addComponent(jCheckBox6)
                            .addGroup(jPanel16Layout.createSequentialGroup()
                                .addComponent(jCheckBox5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(1, 1, 1))))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("PCIe Status"));

        jLabel12.setText("Transmit (writes in Gbps):");

        jTextField3.setText("0.000");

        jTextField4.setText("0.000");

        jLabel13.setText("Receive (reads in Gbps):");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout DataPathPanelLayout = new javax.swing.GroupLayout(DataPathPanel);
        DataPathPanel.setLayout(DataPathPanelLayout);
        DataPathPanelLayout.setHorizontalGroup(
            DataPathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DataPathPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(DataPathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        DataPathPanelLayout.setVerticalGroup(
            DataPathPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DataPathPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ReadWritePanel.setPreferredSize(new java.awt.Dimension(364, 404));

        ReadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Read"));

        offsetTextField1.setText("0x");
        offsetTextField1.setToolTipText("Offset Range 0x0 - 0x0FFF");

        dataTextfield1.setEditable(false);

        executeRWButton1.setText("Read");
        executeRWButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeRWButton1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Data:");

        jLabel15.setText("Offset:");

        jLabel5.setText("BAR 4");

        javax.swing.GroupLayout ReadPanelLayout = new javax.swing.GroupLayout(ReadPanel);
        ReadPanel.setLayout(ReadPanelLayout);
        ReadPanelLayout.setHorizontalGroup(
            ReadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15)
                .addGap(6, 6, 6)
                .addComponent(offsetTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataTextfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(executeRWButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ReadPanelLayout.setVerticalGroup(
            ReadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReadPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(ReadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(offsetTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataTextfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(executeRWButton1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel15)
                    .addComponent(jLabel5))
                .addGap(5, 5, 5))
        );

        WritePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Write"));

        offsetTextField.setText("0x");
        offsetTextField.setToolTipText("Offset Range: 0x0 - 0x0FFF");

        dataTextfield.setText("0x");
        dataTextfield.setToolTipText("");

        executeRWButton.setText("Write");
        executeRWButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeRWButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Data:");

        jLabel6.setText("Offset:");

        jLabel7.setText("BAR 4");

        javax.swing.GroupLayout WritePanelLayout = new javax.swing.GroupLayout(WritePanel);
        WritePanel.setLayout(WritePanelLayout);
        WritePanelLayout.setHorizontalGroup(
            WritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WritePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(5, 5, 5)
                .addComponent(offsetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(executeRWButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        WritePanelLayout.setVerticalGroup(
            WritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WritePanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(WritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(offsetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(executeRWButton)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(5, 5, 5))
        );

        hexdumppanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hex Dump"));

        executeBarButton.setText("Dump");
        executeBarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeBarButtonActionPerformed(evt);
            }
        });

        AddressTextField.setText("0x");
        AddressTextField.setToolTipText("Offset Range 0x0 - 0x0FFF");

        sizeControlTextField.setToolTipText("Size in bytes (decimal)");
        sizeControlTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeControlTextFieldActionPerformed(evt);
            }
        });

        jLabel16.setText("Offset:");

        jLabel21.setText("Size:");

        jScrollPane1.setMaximumSize(new java.awt.Dimension(452, 402));

        bardump.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        bardump.setMaximumSize(new java.awt.Dimension(375, 360));
        jScrollPane1.setViewportView(bardump);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
        );

        jLabel14.setText("BAR 4");

        javax.swing.GroupLayout hexdumppanelLayout = new javax.swing.GroupLayout(hexdumppanel);
        hexdumppanel.setLayout(hexdumppanelLayout);
        hexdumppanelLayout.setHorizontalGroup(
            hexdumppanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hexdumppanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(hexdumppanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(hexdumppanelLayout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16)
                        .addGap(4, 4, 4)
                        .addComponent(AddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sizeControlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(executeBarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        hexdumppanelLayout.setVerticalGroup(
            hexdumppanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hexdumppanelLayout.createSequentialGroup()
                .addGroup(hexdumppanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sizeControlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(executeBarButton)
                    .addComponent(jLabel16)
                    .addComponent(jLabel21)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout ReadWritePanelLayout = new javax.swing.GroupLayout(ReadWritePanel);
        ReadWritePanel.setLayout(ReadWritePanelLayout);
        ReadWritePanelLayout.setHorizontalGroup(
            ReadWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReadWritePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ReadWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hexdumppanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(WritePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ReadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        ReadWritePanelLayout.setVerticalGroup(
            ReadWritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReadWritePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ReadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(WritePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(hexdumppanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(40, 40, 40))
        );

        DataPathPanelForOneDP.setPreferredSize(new java.awt.Dimension(364, 404));

        jPanel25.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"srcSGLBDs", "0.000", "0.000"},
                {"destSGLBDs", "0", "0"},
                {"srcStatsBD", "1999", "1999"},
                {"destStatsBD", "0", "0"},
                {"Buffers", "0", "0"},
                {"srcErrs", "0", "0"},
                {"destErrs", "0", "0"},
                {"internalErrs", "0", "0"}
            },
            new String [] {
                "Parameters", "Transmit(S2C0)", "Receive(S2C0)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(jTable6);

        jLabel17.setText("Data Path-0:");

        loopBackCheckbox.setText("Loopback");
        loopBackCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopBackCheckboxActionPerformed(evt);
            }
        });

        CheckerChcekBox.setText("HW Checker");
        CheckerChcekBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckerChcekBoxActionPerformed(evt);
            }
        });

        GeneratorCheckbox.setText("HW Generator");

        jLabel18.setText("Packet Size (bytes):");

        sizeTextField.setText("32768");
        sizeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeTextFieldActionPerformed(evt);
            }
        });

        jbuttonEngStart.setText("Start");
        jbuttonEngStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbuttonEngStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loopBackCheckbox)
                            .addComponent(GeneratorCheckbox)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(CheckerChcekBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbuttonEngStart)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(1, 1, 1))))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(loopBackCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CheckerChcekBox, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbuttonEngStart))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GeneratorCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("PCIe Status"));

        jLabel19.setText("Transmit (writes in Gbps):");

        jTextField7.setEditable(false);
        jTextField7.setText("0.000");

        jTextField8.setEditable(false);
        jTextField8.setText("0.000");

        jLabel20.setText("Receive (reads in Gbps):");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout DataPathPanelForOneDPLayout = new javax.swing.GroupLayout(DataPathPanelForOneDP);
        DataPathPanelForOneDP.setLayout(DataPathPanelForOneDPLayout);
        DataPathPanelForOneDPLayout.setHorizontalGroup(
            DataPathPanelForOneDPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DataPathPanelForOneDPLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(DataPathPanelForOneDPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        DataPathPanelForOneDPLayout.setVerticalGroup(
            DataPathPanelForOneDPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DataPathPanelForOneDPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(185, 185, 185))
        );

        blockdiagramlbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        blockdiagramlbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/BlockDiagram.png"))); // NOI18N

        javax.swing.GroupLayout BlockDiagramPanelLayout = new javax.swing.GroupLayout(BlockDiagramPanel);
        BlockDiagramPanel.setLayout(BlockDiagramPanelLayout);
        BlockDiagramPanelLayout.setHorizontalGroup(
            BlockDiagramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(blockdiagramlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        BlockDiagramPanelLayout.setVerticalGroup(
            BlockDiagramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(blockdiagramlbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        barComboBoxbottom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BAR 2", "BAR 4" }));
        barComboBoxbottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barComboBoxbottomActionPerformed(evt);
            }
        });

        barComboBoxTop.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BAR 2", "BAR 4" }));

        barComboBoxTop1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "BAR 2", "BAR 4" }));
        barComboBoxTop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barComboBoxTop1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout barsControlPlaneLayout = new javax.swing.GroupLayout(barsControlPlane);
        barsControlPlane.setLayout(barsControlPlaneLayout);
        barsControlPlaneLayout.setHorizontalGroup(
            barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(barsControlPlaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(barComboBoxbottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(barsControlPlaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(barComboBoxTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(barsControlPlaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(barComboBoxTop1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        barsControlPlaneLayout.setVerticalGroup(
            barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(barsControlPlaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(barComboBoxbottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(barsControlPlaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(barComboBoxTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(barsControlPlaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(barsControlPlaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(barComboBoxTop1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Kintex UltraScale PCIe Design Control & Monitoring GUI");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                MainScreen.this.windowClosing(evt);
            }
        });

        javax.swing.GroupLayout ControlPanelLayout = new javax.swing.GroupLayout(ControlPanel);
        ControlPanel.setLayout(ControlPanelLayout);
        ControlPanelLayout.setHorizontalGroup(
            ControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 518, Short.MAX_VALUE)
        );
        ControlPanelLayout.setVerticalGroup(
            ControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
        );

        logscrollpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Message Log"));

        logArea.setEditable(false);
        logArea.setColumns(20);
        logArea.setRows(5);
        logscrollpanel.setViewportView(logArea);

        DDR4label.setText("DDR4");

        javax.swing.GroupLayout ddrpanelLayout = new javax.swing.GroupLayout(ddrpanel);
        ddrpanel.setLayout(ddrpanelLayout);
        ddrpanelLayout.setHorizontalGroup(
            ddrpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ddrpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ddricon, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DDR4label, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
        );
        ddrpanelLayout.setVerticalGroup(
            ddrpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ddrpanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(ddrpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DDR4label)
                    .addComponent(ddricon)))
        );

        phy0label.setText("10G PHY-0");

        javax.swing.GroupLayout phy0panelLayout = new javax.swing.GroupLayout(phy0panel);
        phy0panel.setLayout(phy0panelLayout);
        phy0panelLayout.setHorizontalGroup(
            phy0panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(phy0panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(phy0icon, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phy0label)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        phy0panelLayout.setVerticalGroup(
            phy0panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, phy0panelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(phy0panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(phy0label)
                    .addComponent(phy0icon)))
        );

        phty1label.setText("10G PHY-1");

        javax.swing.GroupLayout phy1panelLayout = new javax.swing.GroupLayout(phy1panel);
        phy1panel.setLayout(phy1panelLayout);
        phy1panelLayout.setHorizontalGroup(
            phy1panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(phy1panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(phy1icon, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phty1label)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        phy1panelLayout.setVerticalGroup(
            phy1panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, phy1panelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(phy1panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(phty1label)
                    .addComponent(phy1icon)))
        );

        javax.swing.GroupLayout ledPanelLayout = new javax.swing.GroupLayout(ledPanel);
        ledPanel.setLayout(ledPanelLayout);
        ledPanelLayout.setHorizontalGroup(
            ledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ledPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(ddrpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(phy0panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(phy1panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ledPanelLayout.setVerticalGroup(
            ledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ledPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(ledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddrpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phy0panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phy1panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout DyPanelLayout = new javax.swing.GroupLayout(DyPanel);
        DyPanel.setLayout(DyPanelLayout);
        DyPanelLayout.setHorizontalGroup(
            DyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DyPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(DyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ledPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logscrollpanel)
                    .addComponent(ControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        DyPanelLayout.setVerticalGroup(
            DyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ledPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ControlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logscrollpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tempvaluePanel.setBackground(java.awt.Color.black);
        tempvaluePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tempvaluePanel.setToolTipText("Temp");

        TempMeasureLabel.setBackground(java.awt.Color.black);

        javax.swing.GroupLayout tempvaluePanelLayout = new javax.swing.GroupLayout(tempvaluePanel);
        tempvaluePanel.setLayout(tempvaluePanelLayout);
        tempvaluePanelLayout.setHorizontalGroup(
            tempvaluePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tempvaluePanelLayout.createSequentialGroup()
                .addComponent(MajorTempLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(MinorTempLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(TempMeasureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        tempvaluePanelLayout.setVerticalGroup(
            tempvaluePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tempvaluePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(tempvaluePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MinorTempLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TempMeasureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MajorTempLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Die Temp :");

        javax.swing.GroupLayout tempholdPanelLayout = new javax.swing.GroupLayout(tempholdPanel);
        tempholdPanel.setLayout(tempholdPanelLayout);
        tempholdPanelLayout.setHorizontalGroup(
            tempholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tempholdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(tempvaluePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        tempholdPanelLayout.setVerticalGroup(
            tempholdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tempholdPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(tempvaluePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tempholdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                .addContainerGap())
        );

        PowerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout PowerPanelLayout = new javax.swing.GroupLayout(PowerPanel);
        PowerPanel.setLayout(PowerPanelLayout);
        PowerPanelLayout.setHorizontalGroup(
            PowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 492, Short.MAX_VALUE)
        );
        PowerPanelLayout.setVerticalGroup(
            PowerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 393, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 454, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout sysmonpanelLayout = new javax.swing.GroupLayout(sysmonpanel);
        sysmonpanel.setLayout(sysmonpanelLayout);
        sysmonpanelLayout.setHorizontalGroup(
            sysmonpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sysmonpanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sysmonpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sysmonpanelLayout.createSequentialGroup()
                        .addGroup(sysmonpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PowerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sysmonpanelLayout.createSequentialGroup()
                        .addComponent(tempholdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37))))
        );
        sysmonpanelLayout.setVerticalGroup(
            sysmonpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sysmonpanelLayout.createSequentialGroup()
                .addGroup(sysmonpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sysmonpanelLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(tempholdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PowerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(sysmonpanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(388, 388, 388)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPanel.addTab("System Monitor", sysmonpanel);

        javax.swing.GroupLayout topChartperfpanelLayout = new javax.swing.GroupLayout(topChartperfpanel);
        topChartperfpanel.setLayout(topChartperfpanelLayout);
        topChartperfpanelLayout.setHorizontalGroup(
            topChartperfpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        topChartperfpanelLayout.setVerticalGroup(
            topChartperfpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout bottomChartperfpanelLayout = new javax.swing.GroupLayout(bottomChartperfpanel);
        bottomChartperfpanel.setLayout(bottomChartperfpanelLayout);
        bottomChartperfpanelLayout.setHorizontalGroup(
            bottomChartperfpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 534, Short.MAX_VALUE)
        );
        bottomChartperfpanelLayout.setVerticalGroup(
            bottomChartperfpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 246, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PerformancePlotTabLayout = new javax.swing.GroupLayout(PerformancePlotTab);
        PerformancePlotTab.setLayout(PerformancePlotTabLayout);
        PerformancePlotTabLayout.setHorizontalGroup(
            PerformancePlotTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PerformancePlotTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PerformancePlotTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bottomChartperfpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(topChartperfpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        PerformancePlotTabLayout.setVerticalGroup(
            PerformancePlotTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PerformancePlotTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topChartperfpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bottomChartperfpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPanel.addTab("Performance Plots", PerformancePlotTab);

        PcieEndStatuspanel.setBorder(javax.swing.BorderFactory.createTitledBorder("PCIe Endpoint Status"));

        pcieSysmontable.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        pcieSysmontable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Link State", "Up"},
                {"Link Speed", "5 Gbps"},
                {"Link Width", "x8"},
                {"Interrupts", "Legacy"},
                {"Vendor ID", "0x10ee"},
                {"Device ID", "0x7082"},
                {"MPS(Bytes)", "128"},
                {"MRPS(Bytes)", "512"}
            },
            new String [] {
                "Type", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(pcieSysmontable);

        javax.swing.GroupLayout PcieEndStatuspanelLayout = new javax.swing.GroupLayout(PcieEndStatuspanel);
        PcieEndStatuspanel.setLayout(PcieEndStatuspanelLayout);
        PcieEndStatuspanelLayout.setHorizontalGroup(
            PcieEndStatuspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PcieEndStatuspanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PcieEndStatuspanelLayout.setVerticalGroup(
            PcieEndStatuspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        hostCreditsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Host System's Initial Credits"));

        hostsysmontable.setFont(new java.awt.Font("Cantarell", 0, 12)); // NOI18N
        hostsysmontable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Posted Header", "96"},
                {"Non Posted Header", "96"},
                {"Completion Header", "0"},
                {null, null},
                {null, null},
                {"Posted Data", "432"},
                {"Non Posted Data", "16"},
                {"Completion Data", "0"}
            },
            new String [] {
                "Type", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        hostsysmontable.setRowSelectionAllowed(false);
        jScrollPane3.setViewportView(hostsysmontable);

        javax.swing.GroupLayout hostCreditsPanelLayout = new javax.swing.GroupLayout(hostCreditsPanel);
        hostCreditsPanel.setLayout(hostCreditsPanelLayout);
        hostCreditsPanelLayout.setHorizontalGroup(
            hostCreditsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hostCreditsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addContainerGap())
        );
        hostCreditsPanelLayout.setVerticalGroup(
            hostCreditsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PcieEndStatuspanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hostCreditsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PcieEndStatuspanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hostCreditsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(180, Short.MAX_VALUE))
        );

        tabbedPanel.addTab("Status and Credits", statusPanel);

        javax.swing.GroupLayout tabpanelLayout = new javax.swing.GroupLayout(tabpanel);
        tabpanel.setLayout(tabpanelLayout);
        tabpanelLayout.setHorizontalGroup(
            tabpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabpanelLayout.createSequentialGroup()
                .addComponent(tabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        tabpanelLayout.setVerticalGroup(
            tabpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabpanelLayout.createSequentialGroup()
                .addComponent(tabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        tabbedPanel.getAccessibleContext().setAccessibleName("System Monitor");

        headinglable.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        headinglable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headinglable.setText("Performance Mode (GEN/CHK)");

        blockdiagrambutton.setText("Block Diagram");
        blockdiagrambutton.setActionCommand("<html><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>B<br>l<br>o<br>c<br>k<br><br>D<br>i<br>a<br>g<br>r<br>a<br>m</html>");
        blockdiagrambutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blockdiagrambuttonActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/xilinx/ultrascale/gui/xlogo_bg.jpg"))); // NOI18N

        javax.swing.GroupLayout HeadingPanelLayout = new javax.swing.GroupLayout(HeadingPanel);
        HeadingPanel.setLayout(HeadingPanelLayout);
        HeadingPanelLayout.setHorizontalGroup(
            HeadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeadingPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel3)
                .addGap(124, 124, 124)
                .addComponent(headinglable, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(blockdiagrambutton, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );
        HeadingPanelLayout.setVerticalGroup(
            HeadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(headinglable, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(blockdiagrambutton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(HeadingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(DyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tabpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(HeadingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(DyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosing
        // TODO add your handling code here:
        powerTimer.cancel();

        if (testStarted) {
            timer.cancel();
            testStarted = false;
            jbuttonEngStart.setText("Start");
            int size = Integer.parseInt(sizeTextField.getText());
            di.stopTest(0, testMode, size);
        }
        System.gc();
        di.flush();
        lp.uninstallDrivers(this);
        showDialog("Removing Device Drivers...Please wait...");

//        lp.showLP();
    }//GEN-LAST:event_windowClosing
    public void unInstallDone() {
        modalDialog.setVisible(false);
        lp.showLP();
        this.dispose();
    }
    JDialog modalDialog;

    private void showDialog(String message) {
        modalDialog = new JDialog(this, "Busy", Dialog.ModalityType.DOCUMENT_MODAL);
        JLabel lmessage = new JLabel(message, JLabel.CENTER);

        //modalDialog.add(limg, BorderLayout.LINE_START);
        modalDialog.add(lmessage, BorderLayout.CENTER);
        modalDialog.setSize(400, 150);
        modalDialog.setLocationRelativeTo(this);
        modalDialog.setVisible(true);
    }

    private void loopBackCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopBackCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loopBackCheckboxActionPerformed

    private void CheckerChcekBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckerChcekBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CheckerChcekBoxActionPerformed

    private void jbuttonEngStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbuttonEngStartActionPerformed
        // TODO add your handling code here:
        if (jbuttonEngStart.getText().equalsIgnoreCase("Start")) {
            jbuttonEngStart.setText("Stop");
            if (CheckerChcekBox.isSelected()) {
                testMode = DriverInfo.CHECKER;
            }
            if (GeneratorCheckbox.isSelected()) {
                testMode = DriverInfo.GENERATOR;
            }
            if (CheckerChcekBox.isSelected() && GeneratorCheckbox.isSelected()) {
                testMode = DriverInfo.CHECKER_GEN;
            }
            int size = Integer.parseInt(sizeTextField.getText());
            di.startTest(0, testMode, size);
            testStarted = true;
            timer = new java.util.Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    makeDMAData();
                }
            }, 0, 3000);
        } else {
            testStarted = false;
            jbuttonEngStart.setText("Start");
            timer.cancel();
            int size = Integer.parseInt(sizeTextField.getText());
            di.stopTest(0, testMode, size);
        }

    }//GEN-LAST:event_jbuttonEngStartActionPerformed

    private void sizeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sizeTextFieldActionPerformed

    private void sizeControlTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeControlTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sizeControlTextFieldActionPerformed

    private void barComboBoxbottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barComboBoxbottomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barComboBoxbottomActionPerformed

    private void executeRWButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeRWButtonActionPerformed
        // TODO add your handling code here:
        // Either read or write.
//        if (readRadioButton.isSelected() == true) {
//            
//        } else {
        // write is selected.
        int bar = 2;
        if (barComboBoxTop.getSelectedItem().equals("Bar2")) {
            bar = 2;
        } else if (barComboBoxTop.getSelectedItem().equals("Bar4")) {
            bar = 4;
        }

        String ofstr = offsetTextField.getText();
        ofstr = ofstr.replaceAll("0x", "");
        int offset = 0;
        try {
            offset = Integer.parseInt(ofstr, 16);
            if (offset % 4 != 0) {// show alert for multiples of zero
                JOptionPane.showMessageDialog(this, "Offset must be a multiple of 4");
                offsetTextField.setText("0x");
                return;
            }
            if (offset >= 0x1000) {
                JOptionPane.showMessageDialog(this, "Max Offset allowed is 0x1000");
                offsetTextField.setText("0x");
                return;
            }
        } catch (Exception e) {// alert for invalid chars
            JOptionPane.showMessageDialog(this, "Offset must be an integer");
            offsetTextField.setText("0x");
            return;
        }
        String data = dataTextfield.getText();
        data = data.replaceAll("0x", "");
        long dataInt = 0;
        try {
            dataInt = Long.parseLong(data, 16);
            /*if (dataInt > 0x10000){
             JOptionPane.showMessageDialog(this, "Max data allowed is 0x10000");
             return;
             }*/
        } catch (Exception e) {// alert for invalid chars
            JOptionPane.showMessageDialog(this, "Data range is 0x0 - 0xFFFFFFFF");
            return;
        }
        di.writeCmd(ms, bar, offset, dataInt);
//        }


    }//GEN-LAST:event_executeRWButtonActionPerformed
    public void fillDataFromRead(int str) {
        dataTextfield1.setText("0x" + Integer.toHexString(str));
    }

    private String leftpad(String s, int l, String p) {
        String tmp = "";
        for (int i = 0; i < l; ++i) {
            tmp = tmp + p;
        }
        return tmp + s;
    }

    public void fillDataDumpFromRead(int[] str) {
        int len = str.length;
        int size = len / 4;
        if (len % 4 > 0) {
            size = size + 1;
        }
        if (size < 14) { // to fill empty rows
            size = 14;
        }
        String[] empty = {"", "", "", "", ""};
        Object[][] bardumpData = new Object[size][5];
        int hindex = 0;
        int index = 1;
        int oindex = 0;
        String hexs = Integer.toHexString(hindex);
        int offsetAdr = Integer.parseInt(AddressTextField.getText().substring(2), 16);
//        System.out.println("offset is "+offsetAdr);
        hindex = offsetAdr;
        hexs = Integer.toHexString(hindex);
        bardumpData[oindex][0] = "0x" + leftpad(hexs, 8 - hexs.length(), "0");
        for (int i = 0; i < str.length; ++i) {
            if (i >= 4 && i % 4 == 0) {
                hindex = hindex + 16;
                hexs = Integer.toHexString(hindex);
                index = 1;
                oindex++;
                bardumpData[oindex][0] = "0x" + leftpad(hexs, 8 - hexs.length(), "0");
            }
            hexs = Integer.toHexString(str[i]);
            bardumpData[oindex][index] = "0x" + leftpad(hexs, 8 - hexs.length(), "0");
            index++;
        }
        // if not exact 4 elements then add the extra ones to here
        for (int i = index; i < 5; ++i) {
            bardumpData[oindex][i] = "";
        }
        // if size is less than 14 add empty values to fill the table
        oindex++;
        for (int i = oindex; i < size; ++i) {
            bardumpData[oindex++] = empty;
        }
        barDumpModel.setData(bardumpData, bardumpNames);
        barDumpModel.fireTableDataChanged();
    }
    private void executeBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeBarButtonActionPerformed
        // TODO add your handling code here:
        // read  dump data here..
        //System.out.println(" clicked execute button");
        int bar = 2;
        if (barComboBoxbottom.getSelectedItem().equals("Bar2")) {
            bar = 2;
        } else if (barComboBoxbottom.getSelectedItem().equals("Bar4")) {
            bar = 4;
        }
        String address = AddressTextField.getText();
        address = address.replaceAll("0x", "");

        int adrs = 0;
        try {
            adrs = Integer.parseInt(address, 16);

        } catch (Exception e) {// alert for invalid chars

            JOptionPane.showMessageDialog(this, "Offset must be an integer");
            AddressTextField.setText("0x");
            return;
        }
        if (adrs >= 0x1000) {
            JOptionPane.showMessageDialog(this, "Max Offset allowed is 0x1000");
            AddressTextField.setText("0x");
            return;
        }
        if (adrs % 4 != 0) {// show alert for multiples of zero
            JOptionPane.showMessageDialog(this, "Offset must be a multiple of 4");
            AddressTextField.setText("0x");
            return;
        }
        String vsizestr = sizeControlTextField.getText();
        int sizev = 0;
        try {
            sizev = Integer.parseInt(vsizestr, 10);

            if (sizev == 0) {
                JOptionPane.showMessageDialog(this, "Size must be greater than 0.");
                sizeControlTextField.setText("");
                return;
            } else if (sizev > 0x1000) {
                JOptionPane.showMessageDialog(this, "Max Size allowed is 4K");
                sizeControlTextField.setText("");
                return;
            } else if (sizev % 4 != 0) {// show alert for multiples of zero
                JOptionPane.showMessageDialog(this, "Size must be a multiple of 4");
                sizeControlTextField.setText("");
                return;
            } else if ((sizev + adrs) > 0x1000) {
                JOptionPane.showMessageDialog(this, "Offset + size should not exceed 4k");
                AddressTextField.setText("0x");
                sizeControlTextField.setText("");
                return;
            }

        } catch (Exception e) {// alert for invalid chars
            JOptionPane.showMessageDialog(this, "Size must be an integer.");
            sizeControlTextField.setText("");
            return;
        }

        di.readDump(ms, bar, adrs, sizev);
    }//GEN-LAST:event_executeBarButtonActionPerformed

    private void executeRWButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeRWButton1ActionPerformed
        // TODO add your handling code here:
        // reading 
        int bar = 2;
        if (barComboBoxTop1.getSelectedItem().equals("Bar2")) {
            bar = 2;
        } else if (barComboBoxTop1.getSelectedItem().equals("Bar4")) {
            bar = 4;
        }

        String ofstr = offsetTextField1.getText();
        ofstr = ofstr.replaceAll("0x", "");
        int offset = 0;
        try {
            offset = Integer.parseInt(ofstr, 16);
            if (offset % 4 != 0) {// show alert for multiples of zero
                JOptionPane.showMessageDialog(this, "Offset must be a multiple of 4");
                offsetTextField1.setText("0x");
                return;
            }
            if (offset >= 0x1000) {
                JOptionPane.showMessageDialog(this, "Max offset allowed is 0x1000");
                offsetTextField1.setText("0x");
                return;
            }
        } catch (Exception e) {// alert for invalid chars
            JOptionPane.showMessageDialog(this, "Offset must be an integer");
            offsetTextField1.setText("0x");
            return;
        }
        di.readCmd(ms, bar, offset);

    }//GEN-LAST:event_executeRWButton1ActionPerformed

    private void blockdiagrambuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blockdiagrambuttonActionPerformed
        // TODO add your handling code here:
        Object[] options1 = {"Close"};
        int s = JOptionPane.showOptionDialog(null, BlockDiagramPanel, "Block Diagram",
                JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options1, null);
    }//GEN-LAST:event_blockdiagrambuttonActionPerformed

    private void barComboBoxTop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barComboBoxTop1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barComboBoxTop1ActionPerformed

    /**
     * @param args the command line arguments
     */
    /* public static void main(String args[]) {
       
     java.awt.EventQueue.invokeLater(new Runnable() {
     public void run() {
     ms = getInstance();
     ms.setVisible(true);
     ms.loadVideo();
     }
     });
     }*/
    public void loadVideo() {
        ControlPanel.add(VideoPanel);
        VideoPanel.setSize(ControlPanel.getSize());
        ControlPanel.repaint();
        ControlPanel.revalidate();

    }
    PowerChart chart1;

    public void loadAllGraphs() {

        String[] labels0 = {"Ultra Scale", ""};

        JPanel PowerGraphPanel = createPanelForGraph();
        chart1 = new PowerChart("Power (W)", PowerGraphPanel.getBackground());

        ChartPanel dialPanel;
        dialPanel = chart1.getChart("");
        dialPanel.setPreferredSize(new Dimension(300, 100));
        PowerGraphPanel.add(dialPanel);

        PowerPanel.add(PowerGraphPanel);

        // loading power graph and running timer
        powerTimer = new java.util.Timer();
        powerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePowerAndTemp();
            }
        }, 0, 3000);

        String[] labels02 = {"Power", ""};
        BarCharts chart2 = new BarCharts("", "", new Color(150, 180, 216), labels0);

        JPanel PowerGraphPanel2 = createPanelForGraph();
        PowerGraphPanel2.add(chart2.getChart(""));
        bottomChartperfpanel.add(PowerGraphPanel2);
        chart2.updateChart(10);
        chart2.updateChart(100);
        chart2.updateChart(500);
        chart2.updateChart(40);
        chart2.updateChart(70);

        String[] labels03 = {"Power", ""};
        BarCharts chart3 = new BarCharts("", "", new Color(150, 180, 216), labels0);

        JPanel PowerGraphPanel3 = createPanelForGraph();
        PowerGraphPanel3.add(chart3.getChart(""));
        topChartperfpanel.add(PowerGraphPanel3);
        chart3.updateChart(10);
        chart3.updateChart(100);
        chart3.updateChart(500);
        chart3.updateChart(40);
        chart3.updateChart(70);
    }

    public JPanel createPanelForGraph() {
        // dimensions
        Dimension dmsns = new Dimension(PowerPanel.getWidth(), PowerPanel.getHeight());
        JPanel dmaStatsPanel = new JPanel();
        dmaStatsPanel.setLayout(new BoxLayout(dmaStatsPanel, BoxLayout.Y_AXIS));
        dmaStatsPanel.setSize(dmsns);
        return dmaStatsPanel;
    }

    public void loadDataPath() {
        ControlPanel.add(DataPathPanel);
        DataPathPanel.setSize(ControlPanel.getSize());
        ControlPanel.repaint();
        ControlPanel.revalidate();

    }

    public void loadDataPathForoneDP() {
        ControlPanel.add(DataPathPanelForOneDP);
        DataPathPanelForOneDP.setSize(ControlPanel.getSize());
        ControlPanel.repaint();
        ControlPanel.revalidate();

    }

    public void loadReadWriteCmd() {
        ControlPanel.add(ReadWritePanel);
        ReadWritePanel.setSize(ControlPanel.getSize());
        ControlPanel.repaint();
        ControlPanel.revalidate();

    }

    public static MainScreen getInstance() {
//        if (ms == null) 
        {
            ms = new MainScreen();
        }
        return ms;
    }

    public void makeDMAData() {

        int ret = di.get_EngineState();

        EngState[] engData = di.getEngState();

        ret = di.get_TRNStats();
        TRNStats trnStats = di.getTRNStats();

        jTextField7.setText(String.format("%2.3f", trnStats.LTX));
        jTextField8.setText(String.format("%2.3f", trnStats.LRX));

        //if (testStarted){
        Object[][] data = {
            {"srcSGLBDs", engData[0].srcSGLBDs, engData[1].srcSGLBDs},
            {"destSGLBDs", engData[0].destSGLBDs, engData[1].destSGLBDs},
            {"srcStatsBD", engData[0].srcStatsBD, engData[1].srcStatsBD},
            {"destStatsBD", engData[0].destStatsBD, engData[1].destStatsBD},
            {"Buffers", engData[0].Buffers, engData[1].Buffers},
            {"srcErrs", engData[0].srcErrs, engData[1].srcErrs},
            {"destErrs", engData[0].destErrs, engData[1].destErrs},
            {"internalErrs", engData[0].internalErrs, engData[1].internalErrs}
        };

        jTable6.setModel(tblModel);
        tblModel.setData(data, dmaColumnNames0);
        tblModel.fireTableDataChanged();
//          jTable6.setModel(tblModel1);
//        tblModel1.setData(data, dmaColumnNames0);
//        tblModel1.fireTableDataChanged();

    }

    public void updatePowerAndTemp() {
        if (Develop.production == 1) {
            int ret = di.get_PowerStats();

            PowerStats ps = di.getPowerStats();

            MajorTempLabel.setIcon(ledicons[ps.die_temp / 10]);
            MinorTempLabel.setIcon(ledicons[ps.die_temp % 10]);
            TempMeasureLabel.setText("" + "°C");

            chart1.updateChart((double) ps.vccint / 1000.0, 
                    (double) ps.mgtvcc / 1000.0,(double) ps.vccaux / 1000.0, (double) ps.vccbram / 1000.0);
        } else { // dummy values
            MajorTempLabel.setIcon(ledicons[32 / 10]);
            MinorTempLabel.setIcon(ledicons[32 % 10]);
            TempMeasureLabel.setText("" + "°C");

            chart1.updateChart((double) 1, (double) 2, (double) 1.5, (double) 2.5);
        }
    }
    static MainScreen ms;
    DriverInfo di;
    MyTableModel tblModel;
    MyTableModel pciemodel;
    MyTableModel hostCredits;
    MyTableModel barDumpModel;
    String[] dmaColumnNames0 = {"Parameters", "Transmit(S2C0)", "Receive(C2S0)"};
    String[] pcieColumnNames = {"Type", "Value"};
    String[] bardumpNames = {"Address", "Value", "Value", "Value", "Value"};
    Object[][] bardumpDummy = {
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""},
        {"", "", "", "", ""}
    };
    Object[][] dummydata = {
        {"srcSGLBDs", 0, 0},
        {"destSGLBDs", 0, 0},
        {"srcStatsBD", 0, 0},
        {"destStatsBD", 0, 0},
        {"Buffers", 0, 0},
        {"srcErrs", 0, 0},
        {"destErrs", 0, 0},
        {"internalErrs", 0, 0}
    };
    String[] pcieEndptClm = {"Type", "Value"};
    Object[][] dataForPCIEDummy = {
        {"Link State", "Up"},
        {"", ""},
        {"Link Speed", "5 Gbps"},
        {"Link Width", "x8"},
        {"", ""},
        {"Vendor ID", "0x10ee"},
        {"Device ID", "0x7082"},
        {"", ""},
        {"MPS(Bytes)", "128"},
        {"MRPS(Bytes)", "512"}

    };
    String[] hostPcie = {"Type", "Value"};
    Object[][] hostPcieDummy = {
        {"Bar", "2"},
        {"Address range", ""},
        {"size", "5 Gbps"},
        {"", ""},
        {"Bar", "4"},
        {"Address range", ""},
        {"size", "5 Gbps"},
        {"", ""},
        {"Bar", "6"},
        {"Address range", ""},
        {"size", "5 Gbps"},};
//     MyTableModel tblModel1;
//    MyTableModel pciemodel;
//    MyTableModel hostCredits;
//    String[] dmaColumnNames1 = {"Parameters", "Transmit(S2C0)", "Receive(C2S0)"};
//    String[] hostColumnNames = {"Type", "Value"};
//    Object[][] dummydata = {
//        {"srcSGLBDs", 0, 0},
//        {"destSGLBDs", 0, 0},
//        {"srcStatsBD", 0, 0},
//        {"destStatsBD", 0, 0},
//        {"Buffers", 0, 0},
//        {"srcErrs", 0, 0},
//        {"destErrs", 0, 0},
//        {"internalErrs", 0, 0}
//    };
//    String[] pcieEndptClm = {"Type", "Value"};
//    Object[][] dataForHostDummy = {
//        {"Link State", "Up"},
//        {"", ""},
//        {"Link Speed", "5 Gbps"},
//        {"Link Width", "x8"},
//        {"", ""},
//        {"Vendor ID", "0x10ee"},
//        {"Device ID", "0x7082"},
//        {"", ""},
//        {"MPS(Bytes)", "128"},
//        {"MRPS(Bytes)", "512"}           

    boolean testStarted;
    int testMode;
    int maxSize;
    ImageIcon[] ledicons = new ImageIcon[10];
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AddressTextField;
    private javax.swing.JPanel BlockDiagramPanel;
    private javax.swing.JCheckBox CheckerChcekBox;
    private javax.swing.JPanel ControlPanel;
    private javax.swing.JLabel DDR4label;
    private javax.swing.JPanel DataPathPanel;
    private javax.swing.JPanel DataPathPanelForOneDP;
    private javax.swing.JPanel DyPanel;
    private javax.swing.JCheckBox GeneratorCheckbox;
    private javax.swing.JPanel HeadingPanel;
    private javax.swing.JLabel MajorTempLabel;
    private javax.swing.JLabel MinorTempLabel;
    private javax.swing.JPanel PcieEndStatuspanel;
    private javax.swing.JPanel PerformancePlotTab;
    private javax.swing.JPanel PowerPanel;
    private javax.swing.JPanel ReadPanel;
    private javax.swing.JPanel ReadWritePanel;
    private javax.swing.JLabel TempMeasureLabel;
    private javax.swing.JPanel VideoPanel;
    private javax.swing.JPanel WritePanel;
    private javax.swing.JComboBox barComboBoxTop;
    private javax.swing.JComboBox barComboBoxTop1;
    private javax.swing.JComboBox barComboBoxbottom;
    private javax.swing.JTable bardump;
    private javax.swing.JPanel barsControlPlane;
    private javax.swing.JButton blockdiagrambutton;
    private javax.swing.JLabel blockdiagramlbl;
    private javax.swing.JPanel bottomChartperfpanel;
    private javax.swing.JPanel bottomVidpanel;
    private javax.swing.JTextField dataTextfield;
    private javax.swing.JTextField dataTextfield1;
    private javax.swing.JLabel ddricon;
    private javax.swing.JPanel ddrpanel;
    private javax.swing.JButton executeBarButton;
    private javax.swing.JButton executeRWButton;
    private javax.swing.JButton executeRWButton1;
    private javax.swing.JLabel headinglable;
    private javax.swing.JPanel hexdumppanel;
    private javax.swing.JPanel hostCreditsPanel;
    private javax.swing.JTable hostsysmontable;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JButton jbuttonEngStart;
    private javax.swing.JPanel ledPanel;
    private javax.swing.JTextArea logArea;
    private javax.swing.JScrollPane logscrollpanel;
    private javax.swing.JCheckBox loopBackCheckbox;
    private javax.swing.JTextField offsetTextField;
    private javax.swing.JTextField offsetTextField1;
    private javax.swing.JTable pcieSysmontable;
    private javax.swing.JLabel phty1label;
    private javax.swing.JLabel phy0icon;
    private javax.swing.JLabel phy0label;
    private javax.swing.JPanel phy0panel;
    private javax.swing.JLabel phy1icon;
    private javax.swing.JPanel phy1panel;
    private javax.swing.JTextField sizeControlTextField;
    private javax.swing.JTextField sizeTextField;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JPanel sysmonpanel;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JPanel tabpanel;
    private javax.swing.JPanel tempholdPanel;
    private javax.swing.JPanel tempvaluePanel;
    private javax.swing.JPanel topChartperfpanel;
    private javax.swing.JPanel topVidpanel;
    // End of variables declaration//GEN-END:variables

}
