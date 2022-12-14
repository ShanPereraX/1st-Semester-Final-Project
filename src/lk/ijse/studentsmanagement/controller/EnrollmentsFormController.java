package lk.ijse.studentsmanagement.controller;

import com.google.zxing.WriterException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import lk.ijse.studentsmanagement.comboLoad.ComboLoader;
import lk.ijse.studentsmanagement.model.BatchModel;
import lk.ijse.studentsmanagement.model.GardianModel;
import lk.ijse.studentsmanagement.model.RegistrationModel;
import lk.ijse.studentsmanagement.qr.QRGenerator;
import lk.ijse.studentsmanagement.util.RegExPatterns;
import lk.ijse.studentsmanagement.smtp.Mail;
import lk.ijse.studentsmanagement.to.Batch;
import lk.ijse.studentsmanagement.to.Gardian;
import lk.ijse.studentsmanagement.to.Payment;
import lk.ijse.studentsmanagement.to.Registration;
import lk.ijse.studentsmanagement.util.Navigation;
import lk.ijse.studentsmanagement.util.Routes;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;

import static lk.ijse.studentsmanagement.autogenerater.AutoGenerateID.setLblRegPaymentID;
import static lk.ijse.studentsmanagement.autogenerater.AutoGenerateID.setRegistrationID;

public class EnrollmentsFormController implements Initializable {

    public Label lblSelectCourse;
    public Label lblInvalidEmail;
    public Label lblSelectDob;
    public Label lblSelectCourseFirst;
    public Label lblInvalidSearchDetail;
    public JFXTextField txtStdNic;
    public Label lblInvalidStdNic;
    public AnchorPane panelRegistration;
    public AnchorPane panelGuardian;
    public AnchorPane panelPayment;
    public JFXButton btnView;
    public JFXButton btnEnroll;
    public Label lblInvalidAmount;
    public Label lblInvalidRemark;
    @FXML
    private AnchorPane pane;

    @FXML
    private JFXTextField txtStdAddress;

    @FXML
    private JFXTextField txtStdCity;

    @FXML
    private JFXTextField txtStdMobileNumber;

    @FXML
    private JFXTextField txtStdEmail;

    @FXML
    private JFXTextField txtSchool;

    @FXML
    private JFXRadioButton rBtnMale;

    @FXML
    private ToggleGroup gender;

    @FXML
    private JFXRadioButton rBtnFemale;

    @FXML
    private JFXDatePicker calDob;

    @FXML
    private JFXTextField txtStdName;

    @FXML
    private JFXRadioButton rBtnMaster;

    @FXML
    private ToggleGroup edu;

    @FXML
    private JFXRadioButton rBtnDegree;

    @FXML
    private JFXRadioButton rBtnDiploma;

    @FXML
    private JFXRadioButton rBtnAL;

    @FXML
    private JFXRadioButton rBtnOL;

    @FXML
    private Label lblRegID;

    @FXML
    private JFXTextField txtPostalCode;

    @FXML
    private Label lblInvalidName;

    @FXML
    private Label lblInvalidAddress;

    @FXML
    private Label lblInvalidCity;

    @FXML
    private Label lblInvalidPostalCode;

    @FXML
    private Label lblInvalidMobileNumber;

    @FXML
    private Label lblInvalidMobileNumber1;

    @FXML
    private Label lblInvalidSchool;

    @FXML
    private JFXRadioButton rBtnYes;

    @FXML
    private ToggleGroup familyMember;

    @FXML
    private JFXRadioButton rBtnNo;

    @FXML
    private JFXTextField txtParentAddress;

    @FXML
    private JFXTextField txtParentMobile;

    @FXML
    private JFXTextField txtParentName;

    @FXML
    private JFXTextField txtParentEmail;

    @FXML
    private JFXTextField txtParentDesignation;

    @FXML
    private JFXTextField txtParentWorkPlace;

    @FXML
    private JFXTextField txtParentID;

    @FXML
    private JFXTextField txtSearchParent;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private Label lblInvalidParentID;

    @FXML
    private Label lblInvalidParentName;

    @FXML
    private Label lblInvalidParentAddress;

    @FXML
    private Label lblInvalidParentMobile;

    @FXML
    private Label lblInvalidParentEmail;

    @FXML
    private Label lblInvalidParentDesignaion;

    @FXML
    private Label lblInvalidParentWorkingPlace;

    @FXML
    private Label lblPaymentID;

    @FXML
    private JFXTextField txtAmount;

    @FXML
    private JFXTextField txtRemark;

    @FXML
    private Label txtBatch;

    @FXML
    private JFXComboBox<String> cmbCourse;

    @FXML
    void btnEnrollClickOnAction(ActionEvent event) {
        boolean isAdded;
        Registration registration = isStdDetailCorrect();
        try {
            if (registration != null) {
                System.out.println("registration not null");
                Gardian gardian = setGardianDetail();
                if (gardian != null) {
                    System.out.println("gardian not null");
                    Payment payment = setPaymentOb();
                    if (payment != null) {
                        System.out.println("payment not null");
                        if (!rBtnYes.isSelected()) {
                            //add with parent
                            System.out.println("add with parent");
                            registration.setPayment(payment);
                            gardian.setRegistration(registration);
                            isAdded = GardianModel.addGardianT(gardian);
                        } else {
                            //add without parent
                            System.out.println("add without parent");
                            registration.setGardianId(txtParentID.getText());
                            registration.setPayment(payment);
                            isAdded = RegistrationModel.registrationPaymentTransaction(registration);
                        }
                        if (isAdded) alertGenerateQRCodeAndPrintBill(registration);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Invalid Payment").showAndWait();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Enter Guardian Details").showAndWait();
                }
                Navigation.navigate(Routes.ENROLLMENTS, pane);
            }
        } catch (SQLException | IOException | ClassNotFoundException | WriterException e) {
            new Alert(Alert.AlertType.ERROR, String.valueOf(e)).show();
        }
    }

    private void alertGenerateQRCodeAndPrintBill(Registration registration) throws IOException, WriterException {
        printReport();
        QRGenerator.getGenerator(registration.toString());
        String msg2 = "\n\n\n\n\nThis email and any attachment transmitted herewith are confidential and is intended solely for the use of the individual or entity to which they are addressed and may contain information that is privileged or otherwise protected from disclosure. If you are not the intended recipient, you are hereby notified that disclosing, copying, distributing, or taking any action in reliance on this email and the information it contains is strictly prohibited. If you have received this email in error, please notify the sender immediately by reply email and discard all of its contents by deleting this email and the attachment, if any, from your system";
        String msg = "\t \t \t WELCOME TO INSTITUTE OF JAVA AND SOFTWARE ENGINEERING \n" +
                "Dear "+registration.getName()+", Greetings from the Student Enrollment Unit!\n\n" +
                "Your Students ID is : " + registration.getRegistrationId() +
                "\n\nThank You!..."+msg2;
        String subject = "Welcome to Institute of Software Engineering";
        try {
            Mail.outMail(msg,registration.getEmail(),subject);
        } catch (MessagingException e) {
            new Alert(Alert.AlertType.INFORMATION,String.valueOf(e)).show();
        }
    }

    private void printReport() {
        HashMap hashMap = new HashMap<>();

        hashMap.put("receptNo", lblPaymentID.getText());
        hashMap.put("regId", lblRegID.getText());
        hashMap.put("name", txtStdName.getText());
        hashMap.put("batchID", txtBatch.getText());
        hashMap.put("remark", txtRemark.getText());
        hashMap.put("amount", txtAmount.getText());
        hashMap.put("total", txtAmount.getText());
        hashMap.put("nic", txtStdNic.getText());
        try {
            JasperReport compileReport = JasperCompileManager.compileReport(
                    JRXmlLoader.load(
                            getClass().getResourceAsStream(
                                    "/lk/ijse/studentsmanagement/report/RegistrationReceipt.jrxml"
                            )
                    )
            );
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, hashMap, new JREmptyDataSource());
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            new Alert(Alert.AlertType.INFORMATION, String.valueOf(e)).show();
        }
    }

    private Payment setPaymentOb() {
        if (RegExPatterns.getDoublePattern().matcher(txtAmount.getText()).matches()) {
            if (RegExPatterns.getNamePattern().matcher(txtRemark.getText()).matches()) {
                return new Payment(
                        lblPaymentID.getText(),
                        lblRegID.getText(),
                        "registration",
                        "Registration Payment",
                        Double.parseDouble(txtAmount.getText()),
                        Date.valueOf(LocalDate.now())
                );
            } else {
                txtRemark.setFocusColor(Color.RED);
                lblInvalidRemark.setVisible(true);
            }
        } else {
            txtAmount.setFocusColor(Color.RED);
            lblInvalidAmount.setVisible(true);
        }
        return null;
    }

    private Gardian setGardianDetail() {
        System.out.println("gardian regex");
        if (RegExPatterns.getOldIDPattern().matcher(txtParentID.getText()).matches()) {
            System.out.println("Id check");
            if (RegExPatterns.getNamePattern().matcher(txtParentName.getText()).matches()) {
                System.out.println("name check");
                if (RegExPatterns.getAddressPattern().matcher(txtParentAddress.getText()).matches()) {
                    System.out.println("address check");
                    if (RegExPatterns.getMobilePattern().matcher(txtParentMobile.getText()).matches()) {
                        System.out.println("mobile check");
                        if (RegExPatterns.getEmailPattern().matcher(txtParentEmail.getText()).matches()) {
                            System.out.println("email check");
                            if (RegExPatterns.getNamePattern().matcher(txtParentDesignation.getText()).matches()) {
                                System.out.println("designation check");
                                if (RegExPatterns.getNamePattern().matcher(txtParentWorkPlace.getText()).matches()) {
                                    System.out.println("work place check");
                                    return new Gardian(
                                            txtParentID.getText(),
                                            txtParentName.getText(),
                                            txtParentAddress.getText(),
                                            txtParentMobile.getText(),
                                            txtParentEmail.getText(),
                                            txtParentDesignation.getText(),
                                            txtParentWorkPlace.getText()
                                    );
                                } else {
                                    lblInvalidParentWorkingPlace.setVisible(true);
                                    txtParentWorkPlace.setFocusColor(Color.RED);
                                }
                            } else {
                                lblInvalidParentDesignaion.setVisible(true);
                                txtParentDesignation.setFocusColor(Color.RED);
                            }
                        } else {
                            lblInvalidParentEmail.setVisible(true);
                            txtParentEmail.setFocusColor(Color.RED);
                        }
                    } else {
                        lblInvalidParentMobile.setVisible(true);
                        txtParentMobile.setFocusColor(Color.RED);
                    }
                } else {
                    lblInvalidParentAddress.setVisible(true);
                    txtParentAddress.setFocusColor(Color.RED);
                }
            } else {
                lblInvalidParentName.setVisible(true);
                txtParentName.setFocusColor(Color.RED);
            }
        } else {
            lblInvalidParentID.setVisible(true);
            txtParentID.setFocusColor(Color.RED);
        }
        return null;
    }

    private Registration isStdDetailCorrect() {
        if (RegExPatterns.getNamePattern().matcher(txtStdName.getText()).matches()) {
            if (RegExPatterns.getIdPattern().matcher(txtStdNic.getText()).matches()) {
                if (RegExPatterns.getAddressPattern().matcher(txtStdAddress.getText()).matches()) {
                    if (RegExPatterns.getCityPattern().matcher(txtStdCity.getText()).matches()) {
                        if (RegExPatterns.getPostalCodePattern().matcher(txtPostalCode.getText()).matches()) {
                            if (RegExPatterns.getMobilePattern().matcher(txtStdMobileNumber.getText()).matches()) {
                                if (RegExPatterns.getEmailPattern().matcher(txtStdEmail.getText()).matches()) {
                                    if (calDob.getValue() != null) {
                                        if (RegExPatterns.getNamePattern().matcher(txtSchool.getText()).matches()) {
                                            if (cmbCourse.getValue() != null) {
                                                return new Registration(

                                                        lblRegID.getText(),
                                                        txtStdNic.getText(),
                                                        txtBatch.getText(),
                                                        cmbCourse.getValue(),
                                                        txtParentID.getText(),
                                                        txtStdName.getText(),
                                                        txtStdAddress.getText(),
                                                        txtStdCity.getText(),
                                                        txtPostalCode.getText(),
                                                        txtStdMobileNumber.getText(),
                                                        txtStdEmail.getText(),
                                                        Date.valueOf(calDob.getValue()),
                                                        (rBtnMale.isSelected()) ? "Male" :
                                                                "Female",
                                                        txtSchool.getText(),
                                                        (rBtnOL.isSelected()) ? "Ordinary Level" :
                                                                (rBtnAL.isSelected()) ? "Advanced Level" :
                                                                        (rBtnDiploma.isSelected()) ? "Diploma Level" :
                                                                                (rBtnDegree.isSelected()) ? "Degree Level" :
                                                                                        "Master",
                                                        "Active"

                                                );
                                            } else {
                                                lblSelectCourseFirst.setVisible(true);
                                            }
                                        } else {
                                            txtSchool.setFocusColor(Color.RED);
                                            lblInvalidSchool.setVisible(true);
                                        }
                                    } else {
                                        lblSelectDob.setVisible(true);
                                    }
                                } else {
                                    txtStdEmail.setFocusColor(Color.RED);
                                    lblInvalidEmail.setVisible(true);
                                }
                            } else {
                                txtStdMobileNumber.setFocusColor(Color.RED);
                                lblInvalidMobileNumber.setVisible(true);
                            }
                        } else {
                            txtPostalCode.setFocusColor(Color.RED);
                            lblInvalidPostalCode.setVisible(true);
                        }
                    } else {
                        txtStdCity.setFocusColor(Color.RED);
                        lblInvalidCity.setVisible(true);
                    }
                } else {
                    txtStdAddress.setFocusColor(Color.RED);
                    lblInvalidAddress.setVisible(true);
                }
            } else {
                txtStdNic.setFocusColor(Color.RED);
                lblInvalidStdNic.setVisible(true);
            }
        } else {
            txtStdName.setFocusColor(Color.RED);
            lblInvalidName.setVisible(true);
        }
        return null;
    }

    @FXML
    void btnSearchOnaction(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (RegExPatterns.getOldIDPattern().matcher(txtSearchParent.getText()).matches()) {
            Gardian gardianDetail = GardianModel.getGardianDetail(new Gardian(txtSearchParent.getText()));
            if (gardianDetail != null) {
                txtParentID.setText(gardianDetail.getId());
                txtParentName.setText(gardianDetail.getName());
                txtParentEmail.setText(gardianDetail.getEmail());
                txtParentMobile.setText(gardianDetail.getMobile());
                txtParentWorkPlace.setText(gardianDetail.getWorkingPlace());
                txtParentAddress.setText(gardianDetail.getAddress());
                txtParentDesignation.setText(gardianDetail.getDesignation());
            } else {
                new Alert(Alert.AlertType.ERROR, "Gardian not Found!").show();
            }
        } else {
            lblInvalidSearchDetail.setVisible(true);
            txtSearchParent.setFocusColor(Color.RED);
        }
    }

    @FXML
    void btnViewOnAction(ActionEvent event) throws IOException {
        Navigation.navigate(Routes.VIEW_REGISTRATION, pane);
    }

    @FXML
    void cmbCourseOnAction(ActionEvent event){
        Batch lastOngoingBatch = null;
        try {
            lastOngoingBatch = BatchModel.getLastOngoingBatch(cmbCourse.getValue());
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, String.valueOf(e)).show();
        }
        if (lastOngoingBatch != null) {
            txtBatch.setText(lastOngoingBatch.getId());
            activate(false);
        } else {
            txtBatch.setText("No any Ongoing Batch");
            activate(true);
        }
    }

    private void activate(boolean active) {
        panelRegistration.setDisable(active);
        panelPayment.setDisable(active);
        panelGuardian.setDisable(active);
        btnEnroll.setDisable(active);
        btnView.setDisable(active);
    }

    @FXML
    void rBtnNoOnAction(ActionEvent event) {
        if (rBtnNo.isSelected()) {
            setTextEnable(false);
        }
    }

    private void setTextEnable(boolean flag) {
        txtParentID.setDisable(flag);
        txtParentName.setDisable(flag);
        txtParentAddress.setDisable(flag);
        txtParentMobile.setDisable(flag);
        txtParentEmail.setDisable(flag);
        txtParentDesignation.setDisable(flag);
        txtParentWorkPlace.setDisable(flag);
        txtSearchParent.setDisable(!flag);
        btnSearch.setDisable(!flag);

    }

    @FXML
    void rBtnYesOnAction(ActionEvent event) {
        if (rBtnYes.isSelected()) {
            setTextEnable(true);
        }
    }

    @FXML
    void txtParentAddressOnAction(MouseEvent event) {
        lblInvalidParentAddress.setVisible(false);
    }

    @FXML
    void txtParentDesignationOnAction(MouseEvent event) {
        lblInvalidParentDesignaion.setVisible(false);
    }

    @FXML
    void txtParentEmailOnAction(MouseEvent event) {
        lblInvalidParentEmail.setVisible(false);
    }

    @FXML
    void txtParentIDOnAction(MouseEvent event) {
        lblInvalidParentID.setVisible(false);
    }

    @FXML
    void txtParentMobileOnAction(MouseEvent event) {
        lblInvalidParentMobile.setVisible(false);
    }

    @FXML
    void txtParentNameOnAction(MouseEvent event) {
        lblInvalidParentName.setVisible(false);
    }

    @FXML
    void txtWorkingPlaceOnAction(MouseEvent event) {
        lblInvalidParentWorkingPlace.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            activate(true);
            ComboLoader.loadCoursesList(cmbCourse);

            setLblRegPaymentID(lblPaymentID);
            setRegistrationID(lblRegID);

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, String.valueOf(e)).show();
        }

    }

    public void txtStdAddressOnMouseClick(MouseEvent mouseEvent) {
        lblInvalidAddress.setVisible(false);
    }

    public void txtStdCityOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidCity.setVisible(false);
    }

    public void txtStdMobileOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidMobileNumber.setVisible(false);
    }

    public void txtStdEmailOnMouseClick(MouseEvent mouseEvent) {
        lblInvalidEmail.setVisible(false);
    }

    public void txtStdSchoolOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidSchool.setVisible(false);
    }

    public void txtStdNameOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidName.setVisible(false);
    }

    public void txtStdPostalCodeOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidPostalCode.setVisible(false);
    }

    public void calDobOnMouseClicked(MouseEvent mouseEvent) {
        lblSelectDob.setVisible(false);
    }

    public void cmbCourseOnMouseClicked(MouseEvent mouseEvent) {
        lblSelectCourseFirst.setVisible(false);
    }

    public void txtSearchOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidSearchDetail.setVisible(false);
    }

    public void txtStdNicOnAction(MouseEvent mouseEvent) {
        lblInvalidStdNic.setVisible(false);
    }

    public void txtAmountOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidAmount.setVisible(false);
    }

    public void txtRegistrationOnMouseClicked(MouseEvent mouseEvent) {
        lblInvalidRemark.setVisible(false);
    }

}



