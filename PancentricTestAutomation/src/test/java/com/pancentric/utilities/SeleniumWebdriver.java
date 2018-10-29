package com.pancentric.utilities;

import com.pancentric.browserstackhelper.BrowserStackHelper;
import com.pancentric.browserstackhelper.SauceLabsHelper;
import com.pancentric.jirahelper.JiraHelper;

//import com.accessibility.AccessibilityScanner;
//import com.accessibility.Result;

import com.pancentric.bromham.BromhamStepDefinitions;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.im4java.process.ProcessStarter;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import javax.imageio.ImageIO;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.StandardStream;

import cucumber.api.Scenario;

import org.openqa.selenium.logging.LogEntry;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.StandardStream;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

import java.net.URI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver.Navigation;

import org.openqa.selenium.remote.CapabilityType;

import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import org.apache.log4j.Logger;

import org.zaproxy.clientapi.core.ClientApi;
import sun.misc.BASE64Encoder;


public class SeleniumWebdriver {
    //what locator actions are available in webdriver

    public String stackTrace;
    public String errorMessage;
    public byte[] screenshot;
    
    public enum Locators {
        xpath,
        id,
        partialid,
        name,
        partialname,
        classname,
        partialclass,
        paritallinktext,
        partialLinkDestination,
        linktext,
        cssSelector,
        attribute,
        partialAttribute,
        tagname;
    }

    public enum trackType {
        event,
        pageview,
        timing,
        transaction,
        item,
        social,
        screenview,
        exception;
        @SuppressWarnings("compatibility:-2847791565942810099")
        private static final long serialVersionUID = 1L;
    }

    //this is our driver that will be used for all selenium actions
    private WebDriver driver;
    private WebDriver driverTwo; // used to load another window
    private int maxTries = 3;
    FirefoxProfile profile = new FirefoxProfile();
    private JavascriptExecutor js;
    private Set<Cookie> allCookies;
    private FileDownloader FileDownload;
    
    private String currentDir = System.getProperty("user.dir");
    //Main screenshot directory
    private String parentScreenShotsLocation = "target/ScreenShots/";
    //Test Screenshot directory
    private String testScreenShotDirectory;
    //Element screenshot paths
    private String baselineScreenShotPath;
    private String actualScreenShotPath;
    private String differenceScreenShotPath;
    //Image files
    public File baselineImageFile;
    public File actualImageFile;

    //our constructor, determining which browser to start with
    
    public SeleniumWebdriver(String browser, String appURL, String scenario) throws Exception {
        // open a new web browser
        newBrowser(browser,appURL,scenario);
    }

    //a method to allow retrieving our driver instance

    public WebDriver getDriver() {
        return driver;
    }
        
    // recreate the browser
    public void refreshPage(){
        driver.navigate().refresh();
    }
    //PageRefresh
    public void pageRefresh() {
        getDriver().navigate().refresh();
    }
        
    public void newBrowser(String browser, String appURL, String scenario) throws Exception {

        String path = "";

        if (browser.equals("Firefox")) {


    		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
    		//System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"C:\\temp\\logs.txt");
    		
    		//DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        	
            //System.setProperty("webdriver.gecko.driver","Drivers/geckodriver.exe");
            // set firefox profile to remove download warnings
            //FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.folderList",2);
            profile.setPreference("browser.download.manager.showWhenStarting",false);
            profile.setPreference("browser.download.dir",path);
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk","bin;application/x-athorware-map;application/x-authorware-seg;application/vnd.adobe.air-application-installer-package+zip;application/x-shockwave-flash;application/vnd.adobe.fxp;application/pdf;application/vnd.cups-ppd;application/x-director;applicaion/vnd.adobe.xdp+xml;application/vnd.adobe.xfdf;audio/x-aac;application/vnd.ahead.space;application/vnd.airzip.filesecure.azf;application/vnd.airzip.filesecure.azs;application/vnd.amazon.ebook;application/vnd.amiga.ami;applicatin/andrew-inset;application/vnd.android.package-archive;application/vnd.anser-web-certificate-issue-initiation;application/vnd.anser-web-funds-transfer-initiation;application/vnd.antix.game-component;application/vnd.apple.installe+xml;application/applixware;application/vnd.hhe.lesson-player;application/vnd.aristanetworks.swi;text/x-asm;application/atomcat+xml;application/atomsvc+xml;application/atom+xml;application/pkix-attr-cert;audio/x-aiff;video/x-msvieo;application/vnd.audiograph;image/vnd.dxf;model/vnd.dwf;text/plain-bas;application/x-bcpio;application/octet-stream;image/bmp;application/x-bittorrent;application/vnd.rim.cod;application/vnd.blueice.multipass;application/vnd.bm;application/x-sh;image/prs.btif;application/vnd.businessobjects;application/x-bzip;application/x-bzip2;application/x-csh;text/x-c;application/vnd.chemdraw+xml;text/css;chemical/x-cdx;chemical/x-cml;chemical/x-csml;application/vn.contact.cmsg;application/vnd.claymore;application/vnd.clonk.c4group;image/vnd.dvb.subtitle;application/cdmi-capability;application/cdmi-container;application/cdmi-domain;application/cdmi-object;application/cdmi-queue;applicationvnd.cluetrust.cartomobile-config;application/vnd.cluetrust.cartomobile-config-pkg;image/x-cmu-raster;model/vnd.collada+xml;text/csv;application/mac-compactpro;application/vnd.wap.wmlc;image/cgm;x-conference/x-cooltalk;image/x-cmx;application/vnd.xara;application/vnd.cosmocaller;application/x-cpio;application/vnd.crick.clicker;application/vnd.crick.clicker.keyboard;application/vnd.crick.clicker.palette;application/vnd.crick.clicker.template;application/vn.crick.clicker.wordbank;application/vnd.criticaltools.wbs+xml;application/vnd.rig.cryptonote;chemical/x-cif;chemical/x-cmdf;application/cu-seeme;application/prs.cww;text/vnd.curl;text/vnd.curl.dcurl;text/vnd.curl.mcurl;text/vnd.crl.scurl;application/vnd.curl.car;application/vnd.curl.pcurl;application/vnd.yellowriver-custom-menu;application/dssc+der;application/dssc+xml;application/x-debian-package;audio/vnd.dece.audio;image/vnd.dece.graphic;video/vnd.dec.hd;video/vnd.dece.mobile;video/vnd.uvvu.mp4;video/vnd.dece.pd;video/vnd.dece.sd;video/vnd.dece.video;application/x-dvi;application/vnd.fdsn.seed;application/x-dtbook+xml;application/x-dtbresource+xml;application/vnd.dvb.ait;applcation/vnd.dvb.service;audio/vnd.digital-winds;image/vnd.djvu;application/xml-dtd;application/vnd.dolby.mlp;application/x-doom;application/vnd.dpgraph;audio/vnd.dra;application/vnd.dreamfactory;audio/vnd.dts;audio/vnd.dts.hd;imag/vnd.dwg;application/vnd.dynageo;application/ecmascript;application/vnd.ecowin.chart;image/vnd.fujixerox.edmics-mmr;image/vnd.fujixerox.edmics-rlc;application/exi;application/vnd.proteus.magazine;application/epub+zip;message/rfc82;application/vnd.enliven;application/vnd.is-xpr;image/vnd.xiff;application/vnd.xfdl;application/emma+xml;application/vnd.ezpix-album;application/vnd.ezpix-package;image/vnd.fst;video/vnd.fvt;image/vnd.fastbidsheet;application/vn.denovo.fcselayout-link;video/x-f4v;video/x-flv;image/vnd.fpx;image/vnd.net-fpx;text/vnd.fmi.flexstor;video/x-fli;application/vnd.fluxtime.clip;application/vnd.fdf;text/x-fortran;application/vnd.mif;application/vnd.framemaker;imae/x-freehand;application/vnd.fsc.weblaunch;application/vnd.frogans.fnc;application/vnd.frogans.ltf;application/vnd.fujixerox.ddd;application/vnd.fujixerox.docuworks;application/vnd.fujixerox.docuworks.binder;application/vnd.fujitu.oasys;application/vnd.fujitsu.oasys2;application/vnd.fujitsu.oasys3;application/vnd.fujitsu.oasysgp;application/vnd.fujitsu.oasysprs;application/x-futuresplash;application/vnd.fuzzysheet;image/g3fax;application/vnd.gmx;model/vn.gtw;application/vnd.genomatix.tuxedo;application/vnd.geogebra.file;application/vnd.geogebra.tool;model/vnd.gdl;application/vnd.geometry-explorer;application/vnd.geonext;application/vnd.geoplan;application/vnd.geospace;applicatio/x-font-ghostscript;application/x-font-bdf;application/x-gtar;application/x-texinfo;application/x-gnumeric;application/vnd.google-earth.kml+xml;application/vnd.google-earth.kmz;application/vnd.grafeq;image/gif;text/vnd.graphviz;aplication/vnd.groove-account;application/vnd.groove-help;application/vnd.groove-identity-message;application/vnd.groove-injector;application/vnd.groove-tool-message;application/vnd.groove-tool-template;application/vnd.groove-vcar;video/h261;video/h263;video/h264;application/vnd.hp-hpid;application/vnd.hp-hps;application/x-hdf;audio/vnd.rip;application/vnd.hbci;application/vnd.hp-jlyt;application/vnd.hp-pcl;application/vnd.hp-hpgl;application/vnd.yamaha.h-script;application/vnd.yamaha.hv-dic;application/vnd.yamaha.hv-voice;application/vnd.hydrostatix.sof-data;application/hyperstudio;application/vnd.hal+xml;text/html;application/vnd.ibm.rights-management;application/vnd.ibm.securecontainer;text/calendar;application/vnd.iccprofile;image/x-icon;application/vnd.igloader;image/ief;application/vnd.immervision-ivp;application/vnd.immervision-ivu;application/reginfo+xml;text/vnd.in3d.3dml;text/vnd.in3d.spot;mode/iges;application/vnd.intergeo;application/vnd.cinderella;application/vnd.intercon.formnet;application/vnd.isac.fcs;application/ipfix;application/pkix-cert;application/pkixcmp;application/pkix-crl;application/pkix-pkipath;applicaion/vnd.insors.igm;application/vnd.ipunplugged.rcprofile;application/vnd.irepository.package+xml;text/vnd.sun.j2me.app-descriptor;application/java-archive;application/java-vm;application/x-java-jnlp-file;application/java-serializd-object;text/x-java-source,java;application/javascript;application/json;application/vnd.joost.joda-archive;video/jpm;image/jpeg;video/jpeg;application/vnd.kahootz;application/vnd.chipnuts.karaoke-mmd;application/vnd.kde.karbon;aplication/vnd.kde.kchart;application/vnd.kde.kformula;application/vnd.kde.kivio;application/vnd.kde.kontour;application/vnd.kde.kpresenter;application/vnd.kde.kspread;application/vnd.kde.kword;application/vnd.kenameaapp;applicatin/vnd.kidspiration;application/vnd.kinar;application/vnd.kodak-descriptor;application/vnd.las.las+xml;application/x-latex;application/vnd.llamagraphics.life-balance.desktop;application/vnd.llamagraphics.life-balance.exchange+xml;application/vnd.jam;application/vnd.lotus-1-2-3;application/vnd.lotus-approach;application/vnd.lotus-freelance;application/vnd.lotus-notes;application/vnd.lotus-organizer;application/vnd.lotus-screencam;application/vnd.lotus-wordro;audio/vnd.lucent.voice;audio/x-mpegurl;video/x-m4v;application/mac-binhex40;application/vnd.macports.portpkg;application/vnd.osgeo.mapguide.package;application/marc;application/marcxml+xml;application/mxf;application/vnd.wolfrm.player;application/mathematica;application/mathml+xml;application/mbox;application/vnd.medcalcdata;application/mediaservercontrol+xml;application/vnd.mediastation.cdkey;application/vnd.mfer;application/vnd.mfmp;model/mesh;appliation/mads+xml;application/mets+xml;application/mods+xml;application/metalink4+xml;application/vnd.ms-powerpoint.template.macroenabled.12;application/vnd.ms-word.document.macroenabled.12;application/vnd.ms-word.template.macroenabed.12;application/vnd.mcd;application/vnd.micrografx.flo;application/vnd.micrografx.igx;application/vnd.eszigno3+xml;application/x-msaccess;video/x-ms-asf;application/x-msdownload;application/vnd.ms-artgalry;application/vnd.ms-ca-compressed;application/vnd.ms-ims;application/x-ms-application;application/x-msclip;image/vnd.ms-modi;application/vnd.ms-fontobject;application/vnd.ms-excel;application/vnd.ms-excel.addin.macroenabled.12;application/vnd.ms-excelsheet.binary.macroenabled.12;application/vnd.ms-excel.template.macroenabled.12;application/vnd.ms-excel.sheet.macroenabled.12;application/vnd.ms-htmlhelp;application/x-mscardfile;application/vnd.ms-lrm;application/x-msmediaview;aplication/x-msmoney;application/vnd.openxmlformats-officedocument.presentationml.presentation;application/vnd.openxmlformats-officedocument.presentationml.slide;application/vnd.openxmlformats-officedocument.presentationml.slideshw;application/vnd.openxmlformats-officedocument.presentationml.template;application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;application/vnd.openxmlformats-officedocument.spreadsheetml.template;application/vnd.openxmformats-officedocument.wordprocessingml.document;application/vnd.openxmlformats-officedocument.wordprocessingml.template;application/x-msbinder;application/vnd.ms-officetheme;application/onenote;audio/vnd.ms-playready.media.pya;vdeo/vnd.ms-playready.media.pyv;application/vnd.ms-powerpoint;application/vnd.ms-powerpoint.addin.macroenabled.12;application/vnd.ms-powerpoint.slide.macroenabled.12;application/vnd.ms-powerpoint.presentation.macroenabled.12;appliation/vnd.ms-powerpoint.slideshow.macroenabled.12;application/vnd.ms-project;application/x-mspublisher;application/x-msschedule;application/x-silverlight-app;application/vnd.ms-pki.stl;application/vnd.ms-pki.seccat;application/vn.visio;video/x-ms-wm;audio/x-ms-wma;audio/x-ms-wax;video/x-ms-wmx;application/x-ms-wmd;application/vnd.ms-wpl;application/x-ms-wmz;video/x-ms-wmv;video/x-ms-wvx;application/x-msmetafile;application/x-msterminal;application/msword;application/x-mswrite;application/vnd.ms-works;application/x-ms-xbap;application/vnd.ms-xpsdocument;audio/midi;application/vnd.ibm.minipay;application/vnd.ibm.modcap;application/vnd.jcp.javame.midlet-rms;application/vnd.tmobile-ivetv;application/x-mobipocket-ebook;application/vnd.mobius.mbk;application/vnd.mobius.dis;application/vnd.mobius.plc;application/vnd.mobius.mqy;application/vnd.mobius.msl;application/vnd.mobius.txf;application/vnd.mobius.daf;tex/vnd.fly;application/vnd.mophun.certificate;application/vnd.mophun.application;video/mj2;audio/mpeg;video/vnd.mpegurl;video/mpeg;application/mp21;audio/mp4;video/mp4;application/mp4;application/vnd.apple.mpegurl;application/vnd.msician;application/vnd.muvee.style;application/xv+xml;application/vnd.nokia.n-gage.data;application/vnd.nokia.n-gage.symbian.install;application/x-dtbncx+xml;application/x-netcdf;application/vnd.neurolanguage.nlu;application/vnd.na;application/vnd.noblenet-directory;application/vnd.noblenet-sealer;application/vnd.noblenet-web;application/vnd.nokia.radio-preset;application/vnd.nokia.radio-presets;text/n3;application/vnd.novadigm.edm;application/vnd.novadim.edx;application/vnd.novadigm.ext;application/vnd.flographit;audio/vnd.nuera.ecelp4800;audio/vnd.nuera.ecelp7470;audio/vnd.nuera.ecelp9600;application/oda;application/ogg;audio/ogg;video/ogg;application/vnd.oma.dd2+xml;applicatin/vnd.oasis.opendocument.text-web;application/oebps-package+xml;application/vnd.intu.qbo;application/vnd.openofficeorg.extension;application/vnd.yamaha.openscoreformat;audio/webm;video/webm;application/vnd.oasis.opendocument.char;application/vnd.oasis.opendocument.chart-template;application/vnd.oasis.opendocument.database;application/vnd.oasis.opendocument.formula;application/vnd.oasis.opendocument.formula-template;application/vnd.oasis.opendocument.grapics;application/vnd.oasis.opendocument.graphics-template;application/vnd.oasis.opendocument.image;application/vnd.oasis.opendocument.image-template;application/vnd.oasis.opendocument.presentation;application/vnd.oasis.opendocumen.presentation-template;application/vnd.oasis.opendocument.spreadsheet;application/vnd.oasis.opendocument.spreadsheet-template;application/vnd.oasis.opendocument.text;application/vnd.oasis.opendocument.text-master;application/vnd.asis.opendocument.text-template;image/ktx;application/vnd.sun.xml.calc;application/vnd.sun.xml.calc.template;application/vnd.sun.xml.draw;application/vnd.sun.xml.draw.template;application/vnd.sun.xml.impress;application/vnd.sun.xl.impress.template;application/vnd.sun.xml.math;application/vnd.sun.xml.writer;application/vnd.sun.xml.writer.global;application/vnd.sun.xml.writer.template;application/x-font-otf;application/vnd.yamaha.openscoreformat.osfpvg+xml;application/vnd.osgi.dp;application/vnd.palm;text/x-pascal;application/vnd.pawaafile;application/vnd.hp-pclxl;application/vnd.picsel;image/x-pcx;image/vnd.adobe.photoshop;application/pics-rules;image/x-pict;application/x-chat;aplication/pkcs10;application/x-pkcs12;application/pkcs7-mime;application/pkcs7-signature;application/x-pkcs7-certreqresp;application/x-pkcs7-certificates;application/pkcs8;application/vnd.pocketlearn;image/x-portable-anymap;image/-portable-bitmap;application/x-font-pcf;application/font-tdpfr;application/x-chess-pgn;image/x-portable-graymap;image/png;image/x-portable-pixmap;application/pskc+xml;application/vnd.ctc-posml;application/postscript;application/xfont-type1;application/vnd.powerbuilder6;application/pgp-encrypted;application/pgp-signature;application/vnd.previewsystems.box;application/vnd.pvi.ptid1;application/pls+xml;application/vnd.pg.format;application/vnd.pg.osasli;tex/prs.lines.tag;application/x-font-linux-psf;application/vnd.publishare-delta-tree;application/vnd.pmi.widget;application/vnd.quark.quarkxpress;application/vnd.epson.esf;application/vnd.epson.msf;application/vnd.epson.ssf;applicaton/vnd.epson.quickanime;application/vnd.intu.qfx;video/quicktime;application/x-rar-compressed;audio/x-pn-realaudio;audio/x-pn-realaudio-plugin;application/rsd+xml;application/vnd.rn-realmedia;application/vnd.realvnc.bed;applicatin/vnd.recordare.musicxml;application/vnd.recordare.musicxml+xml;application/relax-ng-compact-syntax;application/vnd.data-vision.rdz;application/rdf+xml;application/vnd.cloanto.rp9;application/vnd.jisp;application/rtf;text/richtex;application/vnd.route66.link66+xml;application/rss+xml;application/shf+xml;application/vnd.sailingtracker.track;image/svg+xml;application/vnd.sus-calendar;application/sru+xml;application/set-payment-initiation;application/set-reistration-initiation;application/vnd.sema;application/vnd.semd;application/vnd.semf;application/vnd.seemail;application/x-font-snf;application/scvp-vp-request;application/scvp-vp-response;application/scvp-cv-request;application/svp-cv-response;application/sdp;text/x-setext;video/x-sgi-movie;application/vnd.shana.informed.formdata;application/vnd.shana.informed.formtemplate;application/vnd.shana.informed.interchange;application/vnd.shana.informed.package;application/thraud+xml;application/x-shar;image/x-rgb;application/vnd.epson.salt;application/vnd.accpac.simply.aso;application/vnd.accpac.simply.imp;application/vnd.simtech-mindmapper;application/vnd.commonspace;application/vnd.ymaha.smaf-audio;application/vnd.smaf;application/vnd.yamaha.smaf-phrase;application/vnd.smart.teacher;application/vnd.svd;application/sparql-query;application/sparql-results+xml;application/srgs;application/srgs+xml;application/sml+xml;application/vnd.koan;text/sgml;application/vnd.stardivision.calc;application/vnd.stardivision.draw;application/vnd.stardivision.impress;application/vnd.stardivision.math;application/vnd.stardivision.writer;application/vnd.tardivision.writer-global;application/vnd.stepmania.stepchart;application/x-stuffit;application/x-stuffitx;application/vnd.solent.sdkm+xml;application/vnd.olpc-sugar;audio/basic;application/vnd.wqd;application/vnd.symbian.install;application/smil+xml;application/vnd.syncml+xml;application/vnd.syncml.dm+wbxml;application/vnd.syncml.dm+xml;application/x-sv4cpio;application/x-sv4crc;application/sbml+xml;text/tab-separated-values;image/tiff;application/vnd.to.intent-module-archive;application/x-tar;application/x-tcl;application/x-tex;application/x-tex-tfm;application/tei+xml;text/plain;application/vnd.spotfire.dxp;application/vnd.spotfire.sfs;application/timestamped-data;applicationvnd.trid.tpt;application/vnd.triscape.mxs;text/troff;application/vnd.trueapp;application/x-font-ttf;text/turtle;application/vnd.umajin;application/vnd.uoml+xml;application/vnd.unity;application/vnd.ufdl;text/uri-list;application/nd.uiq.theme;application/x-ustar;text/x-uuencode;text/x-vcalendar;text/x-vcard;application/x-cdlink;application/vnd.vsf;model/vrml;application/vnd.vcx;model/vnd.mts;model/vnd.vtu;application/vnd.visionary;video/vnd.vivo;applicatin/ccxml+xml,;application/voicexml+xml;application/x-wais-source;application/vnd.wap.wbxml;image/vnd.wap.wbmp;audio/x-wav;application/davmount+xml;application/x-font-woff;application/wspolicy+xml;image/webp;application/vnd.webturb;application/widget;application/winhlp;text/vnd.wap.wml;text/vnd.wap.wmlscript;application/vnd.wap.wmlscriptc;application/vnd.wordperfect;application/vnd.wt.stf;application/wsdl+xml;image/x-xbitmap;image/x-xpixmap;image/x-xwindowump;application/x-x509-ca-cert;application/x-xfig;application/xhtml+xml;application/xml;application/xcap-diff+xml;application/xenc+xml;application/patch-ops-error+xml;application/resource-lists+xml;application/rls-services+xml;aplication/resource-lists-diff+xml;application/xslt+xml;application/xop+xml;application/x-xpinstall;application/xspf+xml;application/vnd.mozilla.xul+xml;chemical/x-xyz;text/yaml;application/yang;application/yin+xml;application/vnd.ul;application/zip;application/vnd.handheld-entertainment+xml;application/vnd.zzazz.deck+xml");
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk","bin;application/x-athorware-map;application/x-authorware-seg;application/vnd.adobe.air-application-installer-package+zip;application/x-shockwave-flash;application/vnd.adobe.fxp;application/pdf;application/vnd.cups-ppd;application/x-director;applicaion/vnd.adobe.xdp+xml;application/vnd.adobe.xfdf;audio/x-aac;application/vnd.ahead.space;application/vnd.airzip.filesecure.azf;application/vnd.airzip.filesecure.azs;application/vnd.amazon.ebook;application/vnd.amiga.ami;applicatin/andrew-inset;application/vnd.android.package-archive;application/vnd.anser-web-certificate-issue-initiation;application/vnd.anser-web-funds-transfer-initiation;application/vnd.antix.game-component;application/vnd.apple.installe+xml;application/applixware;application/vnd.hhe.lesson-player;application/vnd.aristanetworks.swi;text/x-asm;application/atomcat+xml;application/atomsvc+xml;application/atom+xml;application/pkix-attr-cert;audio/x-aiff;video/x-msvieo;application/vnd.audiograph;image/vnd.dxf;model/vnd.dwf;text/plain-bas;application/x-bcpio;application/octet-stream;image/bmp;application/x-bittorrent;application/vnd.rim.cod;application/vnd.blueice.multipass;application/vnd.bm;application/x-sh;image/prs.btif;application/vnd.businessobjects;application/x-bzip;application/x-bzip2;application/x-csh;text/x-c;application/vnd.chemdraw+xml;text/css;chemical/x-cdx;chemical/x-cml;chemical/x-csml;application/vn.contact.cmsg;application/vnd.claymore;application/vnd.clonk.c4group;image/vnd.dvb.subtitle;application/cdmi-capability;application/cdmi-container;application/cdmi-domain;application/cdmi-object;application/cdmi-queue;applicationvnd.cluetrust.cartomobile-config;application/vnd.cluetrust.cartomobile-config-pkg;image/x-cmu-raster;model/vnd.collada+xml;text/csv;application/mac-compactpro;application/vnd.wap.wmlc;image/cgm;x-conference/x-cooltalk;image/x-cmx;application/vnd.xara;application/vnd.cosmocaller;application/x-cpio;application/vnd.crick.clicker;application/vnd.crick.clicker.keyboard;application/vnd.crick.clicker.palette;application/vnd.crick.clicker.template;application/vn.crick.clicker.wordbank;application/vnd.criticaltools.wbs+xml;application/vnd.rig.cryptonote;chemical/x-cif;chemical/x-cmdf;application/cu-seeme;application/prs.cww;text/vnd.curl;text/vnd.curl.dcurl;text/vnd.curl.mcurl;text/vnd.crl.scurl;application/vnd.curl.car;application/vnd.curl.pcurl;application/vnd.yellowriver-custom-menu;application/dssc+der;application/dssc+xml;application/x-debian-package;audio/vnd.dece.audio;image/vnd.dece.graphic;video/vnd.dec.hd;video/vnd.dece.mobile;video/vnd.uvvu.mp4;video/vnd.dece.pd;video/vnd.dece.sd;video/vnd.dece.video;application/x-dvi;application/vnd.fdsn.seed;application/x-dtbook+xml;application/x-dtbresource+xml;application/vnd.dvb.ait;applcation/vnd.dvb.service;audio/vnd.digital-winds;image/vnd.djvu;application/xml-dtd;application/vnd.dolby.mlp;application/x-doom;application/vnd.dpgraph;audio/vnd.dra;application/vnd.dreamfactory;audio/vnd.dts;audio/vnd.dts.hd;imag/vnd.dwg;application/vnd.dynageo;application/ecmascript;application/vnd.ecowin.chart;image/vnd.fujixerox.edmics-mmr;image/vnd.fujixerox.edmics-rlc;application/exi;application/vnd.proteus.magazine;application/epub+zip;message/rfc82;application/vnd.enliven;application/vnd.is-xpr;image/vnd.xiff;application/vnd.xfdl;application/emma+xml;application/vnd.ezpix-album;application/vnd.ezpix-package;image/vnd.fst;video/vnd.fvt;image/vnd.fastbidsheet;application/vn.denovo.fcselayout-link;video/x-f4v;video/x-flv;image/vnd.fpx;image/vnd.net-fpx;text/vnd.fmi.flexstor;video/x-fli;application/vnd.fluxtime.clip;application/vnd.fdf;text/x-fortran;application/vnd.mif;application/vnd.framemaker;imae/x-freehand;application/vnd.fsc.weblaunch;application/vnd.frogans.fnc;application/vnd.frogans.ltf;application/vnd.fujixerox.ddd;application/vnd.fujixerox.docuworks;application/vnd.fujixerox.docuworks.binder;application/vnd.fujitu.oasys;application/vnd.fujitsu.oasys2;application/vnd.fujitsu.oasys3;application/vnd.fujitsu.oasysgp;application/vnd.fujitsu.oasysprs;application/x-futuresplash;application/vnd.fuzzysheet;image/g3fax;application/vnd.gmx;model/vn.gtw;application/vnd.genomatix.tuxedo;application/vnd.geogebra.file;application/vnd.geogebra.tool;model/vnd.gdl;application/vnd.geometry-explorer;application/vnd.geonext;application/vnd.geoplan;application/vnd.geospace;applicatio/x-font-ghostscript;application/x-font-bdf;application/x-gtar;application/x-texinfo;application/x-gnumeric;application/vnd.google-earth.kml+xml;application/vnd.google-earth.kmz;application/vnd.grafeq;image/gif;text/vnd.graphviz;aplication/vnd.groove-account;application/vnd.groove-help;application/vnd.groove-identity-message;application/vnd.groove-injector;application/vnd.groove-tool-message;application/vnd.groove-tool-template;application/vnd.groove-vcar;video/h261;video/h263;video/h264;application/vnd.hp-hpid;application/vnd.hp-hps;application/x-hdf;audio/vnd.rip;application/vnd.hbci;application/vnd.hp-jlyt;application/vnd.hp-pcl;application/vnd.hp-hpgl;application/vnd.yamaha.h-script;application/vnd.yamaha.hv-dic;application/vnd.yamaha.hv-voice;application/vnd.hydrostatix.sof-data;application/hyperstudio;application/vnd.hal+xml;text/html;application/vnd.ibm.rights-management;application/vnd.ibm.securecontainer;text/calendar;application/vnd.iccprofile;image/x-icon;application/vnd.igloader;image/ief;application/vnd.immervision-ivp;application/vnd.immervision-ivu;application/reginfo+xml;text/vnd.in3d.3dml;text/vnd.in3d.spot;mode/iges;application/vnd.intergeo;application/vnd.cinderella;application/vnd.intercon.formnet;application/vnd.isac.fcs;application/ipfix;application/pkix-cert;application/pkixcmp;application/pkix-crl;application/pkix-pkipath;applicaion/vnd.insors.igm;application/vnd.ipunplugged.rcprofile;application/vnd.irepository.package+xml;text/vnd.sun.j2me.app-descriptor;application/java-archive;application/java-vm;application/x-java-jnlp-file;application/java-serializd-object;text/x-java-source,java;application/javascript;application/json;application/vnd.joost.joda-archive;video/jpm;image/jpeg;video/jpeg;application/vnd.kahootz;application/vnd.chipnuts.karaoke-mmd;application/vnd.kde.karbon;aplication/vnd.kde.kchart;application/vnd.kde.kformula;application/vnd.kde.kivio;application/vnd.kde.kontour;application/vnd.kde.kpresenter;application/vnd.kde.kspread;application/vnd.kde.kword;application/vnd.kenameaapp;applicatin/vnd.kidspiration;application/vnd.kinar;application/vnd.kodak-descriptor;application/vnd.las.las+xml;application/x-latex;application/vnd.llamagraphics.life-balance.desktop;application/vnd.llamagraphics.life-balance.exchange+xml;application/vnd.jam;application/vnd.lotus-1-2-3;application/vnd.lotus-approach;application/vnd.lotus-freelance;application/vnd.lotus-notes;application/vnd.lotus-organizer;application/vnd.lotus-screencam;application/vnd.lotus-wordro;audio/vnd.lucent.voice;audio/x-mpegurl;video/x-m4v;application/mac-binhex40;application/vnd.macports.portpkg;application/vnd.osgeo.mapguide.package;application/marc;application/marcxml+xml;application/mxf;application/vnd.wolfrm.player;application/mathematica;application/mathml+xml;application/mbox;application/vnd.medcalcdata;application/mediaservercontrol+xml;application/vnd.mediastation.cdkey;application/vnd.mfer;application/vnd.mfmp;model/mesh;appliation/mads+xml;application/mets+xml;application/mods+xml;application/metalink4+xml;application/vnd.ms-powerpoint.template.macroenabled.12;application/vnd.ms-word.document.macroenabled.12;application/vnd.ms-word.template.macroenabed.12;application/vnd.mcd;application/vnd.micrografx.flo;application/vnd.micrografx.igx;application/vnd.eszigno3+xml;application/x-msaccess;video/x-ms-asf;application/x-msdownload;application/vnd.ms-artgalry;application/vnd.ms-ca-compressed;application/vnd.ms-ims;application/x-ms-application;application/x-msclip;image/vnd.ms-modi;application/vnd.ms-fontobject;application/vnd.ms-excel;application/vnd.ms-excel.addin.macroenabled.12;application/vnd.ms-excelsheet.binary.macroenabled.12;application/vnd.ms-excel.template.macroenabled.12;application/vnd.ms-excel.sheet.macroenabled.12;application/vnd.ms-htmlhelp;application/x-mscardfile;application/vnd.ms-lrm;application/x-msmediaview;aplication/x-msmoney;application/vnd.openxmlformats-officedocument.presentationml.presentation;application/vnd.openxmlformats-officedocument.presentationml.slide;application/vnd.openxmlformats-officedocument.presentationml.slideshw;application/vnd.openxmlformats-officedocument.presentationml.template;application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;application/vnd.openxmlformats-officedocument.spreadsheetml.template;application/vnd.openxmformats-officedocument.wordprocessingml.document;application/vnd.openxmlformats-officedocument.wordprocessingml.template;application/x-msbinder;application/vnd.ms-officetheme;application/onenote;audio/vnd.ms-playready.media.pya;vdeo/vnd.ms-playready.media.pyv;application/vnd.ms-powerpoint;application/vnd.ms-powerpoint.addin.macroenabled.12;application/vnd.ms-powerpoint.slide.macroenabled.12;application/vnd.ms-powerpoint.presentation.macroenabled.12;appliation/vnd.ms-powerpoint.slideshow.macroenabled.12;application/vnd.ms-project;application/x-mspublisher;application/x-msschedule;application/x-silverlight-app;application/vnd.ms-pki.stl;application/vnd.ms-pki.seccat;application/vn.visio;video/x-ms-wm;audio/x-ms-wma;audio/x-ms-wax;video/x-ms-wmx;application/x-ms-wmd;application/vnd.ms-wpl;application/x-ms-wmz;video/x-ms-wmv;video/x-ms-wvx;application/x-msmetafile;application/x-msterminal;application/msword;application/x-mswrite;application/vnd.ms-works;application/x-ms-xbap;application/vnd.ms-xpsdocument;audio/midi;application/vnd.ibm.minipay;application/vnd.ibm.modcap;application/vnd.jcp.javame.midlet-rms;application/vnd.tmobile-ivetv;application/x-mobipocket-ebook;application/vnd.mobius.mbk;application/vnd.mobius.dis;application/vnd.mobius.plc;application/vnd.mobius.mqy;application/vnd.mobius.msl;application/vnd.mobius.txf;application/vnd.mobius.daf;tex/vnd.fly;application/vnd.mophun.certificate;application/vnd.mophun.application;video/mj2;audio/mpeg;video/vnd.mpegurl;video/mpeg;application/mp21;audio/mp4;video/mp4;application/mp4;application/vnd.apple.mpegurl;application/vnd.msician;application/vnd.muvee.style;application/xv+xml;application/vnd.nokia.n-gage.data;application/vnd.nokia.n-gage.symbian.install;application/x-dtbncx+xml;application/x-netcdf;application/vnd.neurolanguage.nlu;application/vnd.na;application/vnd.noblenet-directory;application/vnd.noblenet-sealer;application/vnd.noblenet-web;application/vnd.nokia.radio-preset;application/vnd.nokia.radio-presets;text/n3;application/vnd.novadigm.edm;application/vnd.novadim.edx;application/vnd.novadigm.ext;application/vnd.flographit;audio/vnd.nuera.ecelp4800;audio/vnd.nuera.ecelp7470;audio/vnd.nuera.ecelp9600;application/oda;application/ogg;audio/ogg;video/ogg;application/vnd.oma.dd2+xml;applicatin/vnd.oasis.opendocument.text-web;application/oebps-package+xml;application/vnd.intu.qbo;application/vnd.openofficeorg.extension;application/vnd.yamaha.openscoreformat;audio/webm;video/webm;application/vnd.oasis.opendocument.char;application/vnd.oasis.opendocument.chart-template;application/vnd.oasis.opendocument.database;application/vnd.oasis.opendocument.formula;application/vnd.oasis.opendocument.formula-template;application/vnd.oasis.opendocument.grapics;application/vnd.oasis.opendocument.graphics-template;application/vnd.oasis.opendocument.image;application/vnd.oasis.opendocument.image-template;application/vnd.oasis.opendocument.presentation;application/vnd.oasis.opendocumen.presentation-template;application/vnd.oasis.opendocument.spreadsheet;application/vnd.oasis.opendocument.spreadsheet-template;application/vnd.oasis.opendocument.text;application/vnd.oasis.opendocument.text-master;application/vnd.asis.opendocument.text-template;image/ktx;application/vnd.sun.xml.calc;application/vnd.sun.xml.calc.template;application/vnd.sun.xml.draw;application/vnd.sun.xml.draw.template;application/vnd.sun.xml.impress;application/vnd.sun.xl.impress.template;application/vnd.sun.xml.math;application/vnd.sun.xml.writer;application/vnd.sun.xml.writer.global;application/vnd.sun.xml.writer.template;application/x-font-otf;application/vnd.yamaha.openscoreformat.osfpvg+xml;application/vnd.osgi.dp;application/vnd.palm;text/x-pascal;application/vnd.pawaafile;application/vnd.hp-pclxl;application/vnd.picsel;image/x-pcx;image/vnd.adobe.photoshop;application/pics-rules;image/x-pict;application/x-chat;aplication/pkcs10;application/x-pkcs12;application/pkcs7-mime;application/pkcs7-signature;application/x-pkcs7-certreqresp;application/x-pkcs7-certificates;application/pkcs8;application/vnd.pocketlearn;image/x-portable-anymap;image/-portable-bitmap;application/x-font-pcf;application/font-tdpfr;application/x-chess-pgn;image/x-portable-graymap;image/png;image/x-portable-pixmap;application/pskc+xml;application/vnd.ctc-posml;application/postscript;application/xfont-type1;application/vnd.powerbuilder6;application/pgp-encrypted;application/pgp-signature;application/vnd.previewsystems.box;application/vnd.pvi.ptid1;application/pls+xml;application/vnd.pg.format;application/vnd.pg.osasli;tex/prs.lines.tag;application/x-font-linux-psf;application/vnd.publishare-delta-tree;application/vnd.pmi.widget;application/vnd.quark.quarkxpress;application/vnd.epson.esf;application/vnd.epson.msf;application/vnd.epson.ssf;applicaton/vnd.epson.quickanime;application/vnd.intu.qfx;video/quicktime;application/x-rar-compressed;audio/x-pn-realaudio;audio/x-pn-realaudio-plugin;application/rsd+xml;application/vnd.rn-realmedia;application/vnd.realvnc.bed;applicatin/vnd.recordare.musicxml;application/vnd.recordare.musicxml+xml;application/relax-ng-compact-syntax;application/vnd.data-vision.rdz;application/rdf+xml;application/vnd.cloanto.rp9;application/vnd.jisp;application/rtf;text/richtex;application/vnd.route66.link66+xml;application/rss+xml;application/shf+xml;application/vnd.sailingtracker.track;image/svg+xml;application/vnd.sus-calendar;application/sru+xml;application/set-payment-initiation;application/set-reistration-initiation;application/vnd.sema;application/vnd.semd;application/vnd.semf;application/vnd.seemail;application/x-font-snf;application/scvp-vp-request;application/scvp-vp-response;application/scvp-cv-request;application/svp-cv-response;application/sdp;text/x-setext;video/x-sgi-movie;application/vnd.shana.informed.formdata;application/vnd.shana.informed.formtemplate;application/vnd.shana.informed.interchange;application/vnd.shana.informed.package;application/thraud+xml;application/x-shar;image/x-rgb;application/vnd.epson.salt;application/vnd.accpac.simply.aso;application/vnd.accpac.simply.imp;application/vnd.simtech-mindmapper;application/vnd.commonspace;application/vnd.ymaha.smaf-audio;application/vnd.smaf;application/vnd.yamaha.smaf-phrase;application/vnd.smart.teacher;application/vnd.svd;application/sparql-query;application/sparql-results+xml;application/srgs;application/srgs+xml;application/sml+xml;application/vnd.koan;text/sgml;application/vnd.stardivision.calc;application/vnd.stardivision.draw;application/vnd.stardivision.impress;application/vnd.stardivision.math;application/vnd.stardivision.writer;application/vnd.tardivision.writer-global;application/vnd.stepmania.stepchart;application/x-stuffit;application/x-stuffitx;application/vnd.solent.sdkm+xml;application/vnd.olpc-sugar;audio/basic;application/vnd.wqd;application/vnd.symbian.install;application/smil+xml;application/vnd.syncml+xml;application/vnd.syncml.dm+wbxml;application/vnd.syncml.dm+xml;application/x-sv4cpio;application/x-sv4crc;application/sbml+xml;text/tab-separated-values;image/tiff;application/vnd.to.intent-module-archive;application/x-tar;application/x-tcl;application/x-tex;application/x-tex-tfm;application/tei+xml;text/plain;application/vnd.spotfire.dxp;application/vnd.spotfire.sfs;application/timestamped-data;applicationvnd.trid.tpt;application/vnd.triscape.mxs;text/troff;application/vnd.trueapp;application/x-font-ttf;text/turtle;application/vnd.umajin;application/vnd.uoml+xml;application/vnd.unity;application/vnd.ufdl;text/uri-list;application/nd.uiq.theme;application/x-ustar;text/x-uuencode;text/x-vcalendar;text/x-vcard;application/x-cdlink;application/vnd.vsf;model/vrml;application/vnd.vcx;model/vnd.mts;model/vnd.vtu;application/vnd.visionary;video/vnd.vivo;applicatin/ccxml+xml,;application/voicexml+xml;application/x-wais-source;application/vnd.wap.wbxml;image/vnd.wap.wbmp;audio/x-wav;application/davmount+xml;application/x-font-woff;application/wspolicy+xml;image/webp;application/vnd.webturb;application/widget;application/winhlp;text/vnd.wap.wml;text/vnd.wap.wmlscript;application/vnd.wap.wmlscriptc;application/vnd.wordperfect;application/vnd.wt.stf;application/wsdl+xml;image/x-xbitmap;image/x-xpixmap;image/x-xwindowump;application/x-x509-ca-cert;application/x-xfig;application/xhtml+xml;application/xml;application/xcap-diff+xml;application/xenc+xml;application/patch-ops-error+xml;application/resource-lists+xml;application/rls-services+xml;aplication/resource-lists-diff+xml;application/xslt+xml;application/xop+xml;application/x-xpinstall;application/xspf+xml;application/vnd.mozilla.xul+xml;chemical/x-xyz;text/yaml;application/yang;application/yin+xml;application/vnd.ul;application/zip;application/vnd.handheld-entertainment+xml;application/vnd.zzazz.deck+xml");
            profile.setPreference("privacy.popups.showBrowserMessage", false);
            profile.setPreference("network.cookie.prefsMigrated", true);
            profile.setPreference("network.cookie.lifetimePolicy", 0);
            profile.setPreference("network.cookie.lifetime.days", 30);
            profile.setPreference("app.update.enabled", false);
            profile.setPreference("app.update.auto", false);
            //profile.setPreference("network.proxy.type", 1);
            //profile.setPreference("network.proxy.http", "localhost");//
            //profile.setPreference("network.proxy.http_port", 7777);
            //driver = new FirefoxDriver(profile);
            
    		// If I comment out this memory leak goes away
    		//capabilities.setCapability(FirefoxDriver.PROFILE, profile);
    		
    		FirefoxOptions option=new FirefoxOptions();
    		option.addPreference("marionette", true); 
    		option.setProfile(profile);
    		// Initialize Firefox driver
    		//driver = new FirefoxDriver();    		
    		//driver = new FirefoxDriver(capabilities); 
            driver = new FirefoxDriver(option);
        }
        
        else if (browser.equals("LocalGrid")) {
            System.setProperty("webdriver.chrome.driver","Drivers/chromedriver");

            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", path);

            DesiredCapabilities capability = new DesiredCapabilities().chrome();
            capability.setCapability("build", "JUnit-Parallel");
            capability.setCapability("name", "Parallel test");
            ChromeOptions options = new ChromeOptions();

            options.addArguments("test-type");
            options.addArguments("disable-popup-blocking");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-extensions");
            options.addArguments("--headless");
            options.addArguments("headless");
            options.addArguments("window-size=1200x600");
            options.addArguments("disable-gpu");
            options.setExperimentalOption("prefs", chromePrefs);
            capability.setCapability(ChromeOptions.CAPABILITY, options);
            capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            driver =  new RemoteWebDriver(new URL("http://172.19.0.3:4441/wd/hub/"), capability);
        }
    
        else if (browser.equals("Chrome")) {
            System.setProperty("webdriver.chrome.driver", "Drivers/chromedriver");
            
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", path);
            
            DesiredCapabilities capability = new DesiredCapabilities().chrome();
            capability.setCapability("build", "JUnit-Parallel");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("test-type");
            options.addArguments("disable-popup-blocking");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-extensions");
            options.setExperimentalOption("prefs", chromePrefs);
            capability.setCapability(ChromeOptions.CAPABILITY, options);
            capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            driver = new ChromeDriver(capability);
        }

    
        // for creating a driver instance on a grid hub running on your local machine
        
        else if (browser.contains("JenkinsDockerGrid")) {

            if (browser.contains("chrome")) {
                System.setProperty("webdriver.chrome.driver","Drivers/chromedriver");

                HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
                chromePrefs.put("profile.default_content_settings.popups", 0);
                chromePrefs.put("download.default_directory", path);
                
                DesiredCapabilities capability = new DesiredCapabilities().chrome();
                capability.setCapability("build", "JUnit-Parallel");
                capability.setCapability("name", "Parallel test");
                ChromeOptions options = new ChromeOptions();

                options.addArguments("test-type");
                options.addArguments("disable-popup-blocking");
                options.addArguments("--start-maximized");
                options.addArguments("--disable-extensions");
                options.addArguments("--headless");
                options.addArguments("headless");
                options.addArguments("window-size=1200x600");
                options.addArguments("disable-gpu");
                options.setExperimentalOption("prefs", chromePrefs);
                capability.setCapability(ChromeOptions.CAPABILITY, options);
                capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                driver =  new RemoteWebDriver(new URL("http://selenium__standalone-chrome:4444/wd/hub/"), capability);
            }
            
            else if (browser.contains("firefox")) {
                DesiredCapabilities capability = new DesiredCapabilities().firefox();
                
                profile.setPreference("browser.download.folderList",2);
                profile.setPreference("browser.download.manager.showWhenStarting",false);
                profile.setPreference("browser.download.dir",path);
                profile.setPreference("browser.helperApps.neverAsk.saveToDisk","bin;application/x-athorware-map;application/x-authorware-seg;application/vnd.adobe.air-application-installer-package+zip;application/x-shockwave-flash;application/vnd.adobe.fxp;application/pdf;application/vnd.cups-ppd;application/x-director;applicaion/vnd.adobe.xdp+xml;application/vnd.adobe.xfdf;audio/x-aac;application/vnd.ahead.space;application/vnd.airzip.filesecure.azf;application/vnd.airzip.filesecure.azs;application/vnd.amazon.ebook;application/vnd.amiga.ami;applicatin/andrew-inset;application/vnd.android.package-archive;application/vnd.anser-web-certificate-issue-initiation;application/vnd.anser-web-funds-transfer-initiation;application/vnd.antix.game-component;application/vnd.apple.installe+xml;application/applixware;application/vnd.hhe.lesson-player;application/vnd.aristanetworks.swi;text/x-asm;application/atomcat+xml;application/atomsvc+xml;application/atom+xml;application/pkix-attr-cert;audio/x-aiff;video/x-msvieo;application/vnd.audiograph;image/vnd.dxf;model/vnd.dwf;text/plain-bas;application/x-bcpio;application/octet-stream;image/bmp;application/x-bittorrent;application/vnd.rim.cod;application/vnd.blueice.multipass;application/vnd.bm;application/x-sh;image/prs.btif;application/vnd.businessobjects;application/x-bzip;application/x-bzip2;application/x-csh;text/x-c;application/vnd.chemdraw+xml;text/css;chemical/x-cdx;chemical/x-cml;chemical/x-csml;application/vn.contact.cmsg;application/vnd.claymore;application/vnd.clonk.c4group;image/vnd.dvb.subtitle;application/cdmi-capability;application/cdmi-container;application/cdmi-domain;application/cdmi-object;application/cdmi-queue;applicationvnd.cluetrust.cartomobile-config;application/vnd.cluetrust.cartomobile-config-pkg;image/x-cmu-raster;model/vnd.collada+xml;text/csv;application/mac-compactpro;application/vnd.wap.wmlc;image/cgm;x-conference/x-cooltalk;image/x-cmx;application/vnd.xara;application/vnd.cosmocaller;application/x-cpio;application/vnd.crick.clicker;application/vnd.crick.clicker.keyboard;application/vnd.crick.clicker.palette;application/vnd.crick.clicker.template;application/vn.crick.clicker.wordbank;application/vnd.criticaltools.wbs+xml;application/vnd.rig.cryptonote;chemical/x-cif;chemical/x-cmdf;application/cu-seeme;application/prs.cww;text/vnd.curl;text/vnd.curl.dcurl;text/vnd.curl.mcurl;text/vnd.crl.scurl;application/vnd.curl.car;application/vnd.curl.pcurl;application/vnd.yellowriver-custom-menu;application/dssc+der;application/dssc+xml;application/x-debian-package;audio/vnd.dece.audio;image/vnd.dece.graphic;video/vnd.dec.hd;video/vnd.dece.mobile;video/vnd.uvvu.mp4;video/vnd.dece.pd;video/vnd.dece.sd;video/vnd.dece.video;application/x-dvi;application/vnd.fdsn.seed;application/x-dtbook+xml;application/x-dtbresource+xml;application/vnd.dvb.ait;applcation/vnd.dvb.service;audio/vnd.digital-winds;image/vnd.djvu;application/xml-dtd;application/vnd.dolby.mlp;application/x-doom;application/vnd.dpgraph;audio/vnd.dra;application/vnd.dreamfactory;audio/vnd.dts;audio/vnd.dts.hd;imag/vnd.dwg;application/vnd.dynageo;application/ecmascript;application/vnd.ecowin.chart;image/vnd.fujixerox.edmics-mmr;image/vnd.fujixerox.edmics-rlc;application/exi;application/vnd.proteus.magazine;application/epub+zip;message/rfc82;application/vnd.enliven;application/vnd.is-xpr;image/vnd.xiff;application/vnd.xfdl;application/emma+xml;application/vnd.ezpix-album;application/vnd.ezpix-package;image/vnd.fst;video/vnd.fvt;image/vnd.fastbidsheet;application/vn.denovo.fcselayout-link;video/x-f4v;video/x-flv;image/vnd.fpx;image/vnd.net-fpx;text/vnd.fmi.flexstor;video/x-fli;application/vnd.fluxtime.clip;application/vnd.fdf;text/x-fortran;application/vnd.mif;application/vnd.framemaker;imae/x-freehand;application/vnd.fsc.weblaunch;application/vnd.frogans.fnc;application/vnd.frogans.ltf;application/vnd.fujixerox.ddd;application/vnd.fujixerox.docuworks;application/vnd.fujixerox.docuworks.binder;application/vnd.fujitu.oasys;application/vnd.fujitsu.oasys2;application/vnd.fujitsu.oasys3;application/vnd.fujitsu.oasysgp;application/vnd.fujitsu.oasysprs;application/x-futuresplash;application/vnd.fuzzysheet;image/g3fax;application/vnd.gmx;model/vn.gtw;application/vnd.genomatix.tuxedo;application/vnd.geogebra.file;application/vnd.geogebra.tool;model/vnd.gdl;application/vnd.geometry-explorer;application/vnd.geonext;application/vnd.geoplan;application/vnd.geospace;applicatio/x-font-ghostscript;application/x-font-bdf;application/x-gtar;application/x-texinfo;application/x-gnumeric;application/vnd.google-earth.kml+xml;application/vnd.google-earth.kmz;application/vnd.grafeq;image/gif;text/vnd.graphviz;aplication/vnd.groove-account;application/vnd.groove-help;application/vnd.groove-identity-message;application/vnd.groove-injector;application/vnd.groove-tool-message;application/vnd.groove-tool-template;application/vnd.groove-vcar;video/h261;video/h263;video/h264;application/vnd.hp-hpid;application/vnd.hp-hps;application/x-hdf;audio/vnd.rip;application/vnd.hbci;application/vnd.hp-jlyt;application/vnd.hp-pcl;application/vnd.hp-hpgl;application/vnd.yamaha.h-script;application/vnd.yamaha.hv-dic;application/vnd.yamaha.hv-voice;application/vnd.hydrostatix.sof-data;application/hyperstudio;application/vnd.hal+xml;text/html;application/vnd.ibm.rights-management;application/vnd.ibm.securecontainer;text/calendar;application/vnd.iccprofile;image/x-icon;application/vnd.igloader;image/ief;application/vnd.immervision-ivp;application/vnd.immervision-ivu;application/reginfo+xml;text/vnd.in3d.3dml;text/vnd.in3d.spot;mode/iges;application/vnd.intergeo;application/vnd.cinderella;application/vnd.intercon.formnet;application/vnd.isac.fcs;application/ipfix;application/pkix-cert;application/pkixcmp;application/pkix-crl;application/pkix-pkipath;applicaion/vnd.insors.igm;application/vnd.ipunplugged.rcprofile;application/vnd.irepository.package+xml;text/vnd.sun.j2me.app-descriptor;application/java-archive;application/java-vm;application/x-java-jnlp-file;application/java-serializd-object;text/x-java-source,java;application/javascript;application/json;application/vnd.joost.joda-archive;video/jpm;image/jpeg;video/jpeg;application/vnd.kahootz;application/vnd.chipnuts.karaoke-mmd;application/vnd.kde.karbon;aplication/vnd.kde.kchart;application/vnd.kde.kformula;application/vnd.kde.kivio;application/vnd.kde.kontour;application/vnd.kde.kpresenter;application/vnd.kde.kspread;application/vnd.kde.kword;application/vnd.kenameaapp;applicatin/vnd.kidspiration;application/vnd.kinar;application/vnd.kodak-descriptor;application/vnd.las.las+xml;application/x-latex;application/vnd.llamagraphics.life-balance.desktop;application/vnd.llamagraphics.life-balance.exchange+xml;application/vnd.jam;application/vnd.lotus-1-2-3;application/vnd.lotus-approach;application/vnd.lotus-freelance;application/vnd.lotus-notes;application/vnd.lotus-organizer;application/vnd.lotus-screencam;application/vnd.lotus-wordro;audio/vnd.lucent.voice;audio/x-mpegurl;video/x-m4v;application/mac-binhex40;application/vnd.macports.portpkg;application/vnd.osgeo.mapguide.package;application/marc;application/marcxml+xml;application/mxf;application/vnd.wolfrm.player;application/mathematica;application/mathml+xml;application/mbox;application/vnd.medcalcdata;application/mediaservercontrol+xml;application/vnd.mediastation.cdkey;application/vnd.mfer;application/vnd.mfmp;model/mesh;appliation/mads+xml;application/mets+xml;application/mods+xml;application/metalink4+xml;application/vnd.ms-powerpoint.template.macroenabled.12;application/vnd.ms-word.document.macroenabled.12;application/vnd.ms-word.template.macroenabed.12;application/vnd.mcd;application/vnd.micrografx.flo;application/vnd.micrografx.igx;application/vnd.eszigno3+xml;application/x-msaccess;video/x-ms-asf;application/x-msdownload;application/vnd.ms-artgalry;application/vnd.ms-ca-compressed;application/vnd.ms-ims;application/x-ms-application;application/x-msclip;image/vnd.ms-modi;application/vnd.ms-fontobject;application/vnd.ms-excel;application/vnd.ms-excel.addin.macroenabled.12;application/vnd.ms-excelsheet.binary.macroenabled.12;application/vnd.ms-excel.template.macroenabled.12;application/vnd.ms-excel.sheet.macroenabled.12;application/vnd.ms-htmlhelp;application/x-mscardfile;application/vnd.ms-lrm;application/x-msmediaview;aplication/x-msmoney;application/vnd.openxmlformats-officedocument.presentationml.presentation;application/vnd.openxmlformats-officedocument.presentationml.slide;application/vnd.openxmlformats-officedocument.presentationml.slideshw;application/vnd.openxmlformats-officedocument.presentationml.template;application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;application/vnd.openxmlformats-officedocument.spreadsheetml.template;application/vnd.openxmformats-officedocument.wordprocessingml.document;application/vnd.openxmlformats-officedocument.wordprocessingml.template;application/x-msbinder;application/vnd.ms-officetheme;application/onenote;audio/vnd.ms-playready.media.pya;vdeo/vnd.ms-playready.media.pyv;application/vnd.ms-powerpoint;application/vnd.ms-powerpoint.addin.macroenabled.12;application/vnd.ms-powerpoint.slide.macroenabled.12;application/vnd.ms-powerpoint.presentation.macroenabled.12;appliation/vnd.ms-powerpoint.slideshow.macroenabled.12;application/vnd.ms-project;application/x-mspublisher;application/x-msschedule;application/x-silverlight-app;application/vnd.ms-pki.stl;application/vnd.ms-pki.seccat;application/vn.visio;video/x-ms-wm;audio/x-ms-wma;audio/x-ms-wax;video/x-ms-wmx;application/x-ms-wmd;application/vnd.ms-wpl;application/x-ms-wmz;video/x-ms-wmv;video/x-ms-wvx;application/x-msmetafile;application/x-msterminal;application/msword;application/x-mswrite;application/vnd.ms-works;application/x-ms-xbap;application/vnd.ms-xpsdocument;audio/midi;application/vnd.ibm.minipay;application/vnd.ibm.modcap;application/vnd.jcp.javame.midlet-rms;application/vnd.tmobile-ivetv;application/x-mobipocket-ebook;application/vnd.mobius.mbk;application/vnd.mobius.dis;application/vnd.mobius.plc;application/vnd.mobius.mqy;application/vnd.mobius.msl;application/vnd.mobius.txf;application/vnd.mobius.daf;tex/vnd.fly;application/vnd.mophun.certificate;application/vnd.mophun.application;video/mj2;audio/mpeg;video/vnd.mpegurl;video/mpeg;application/mp21;audio/mp4;video/mp4;application/mp4;application/vnd.apple.mpegurl;application/vnd.msician;application/vnd.muvee.style;application/xv+xml;application/vnd.nokia.n-gage.data;application/vnd.nokia.n-gage.symbian.install;application/x-dtbncx+xml;application/x-netcdf;application/vnd.neurolanguage.nlu;application/vnd.na;application/vnd.noblenet-directory;application/vnd.noblenet-sealer;application/vnd.noblenet-web;application/vnd.nokia.radio-preset;application/vnd.nokia.radio-presets;text/n3;application/vnd.novadigm.edm;application/vnd.novadim.edx;application/vnd.novadigm.ext;application/vnd.flographit;audio/vnd.nuera.ecelp4800;audio/vnd.nuera.ecelp7470;audio/vnd.nuera.ecelp9600;application/oda;application/ogg;audio/ogg;video/ogg;application/vnd.oma.dd2+xml;applicatin/vnd.oasis.opendocument.text-web;application/oebps-package+xml;application/vnd.intu.qbo;application/vnd.openofficeorg.extension;application/vnd.yamaha.openscoreformat;audio/webm;video/webm;application/vnd.oasis.opendocument.char;application/vnd.oasis.opendocument.chart-template;application/vnd.oasis.opendocument.database;application/vnd.oasis.opendocument.formula;application/vnd.oasis.opendocument.formula-template;application/vnd.oasis.opendocument.grapics;application/vnd.oasis.opendocument.graphics-template;application/vnd.oasis.opendocument.image;application/vnd.oasis.opendocument.image-template;application/vnd.oasis.opendocument.presentation;application/vnd.oasis.opendocumen.presentation-template;application/vnd.oasis.opendocument.spreadsheet;application/vnd.oasis.opendocument.spreadsheet-template;application/vnd.oasis.opendocument.text;application/vnd.oasis.opendocument.text-master;application/vnd.asis.opendocument.text-template;image/ktx;application/vnd.sun.xml.calc;application/vnd.sun.xml.calc.template;application/vnd.sun.xml.draw;application/vnd.sun.xml.draw.template;application/vnd.sun.xml.impress;application/vnd.sun.xl.impress.template;application/vnd.sun.xml.math;application/vnd.sun.xml.writer;application/vnd.sun.xml.writer.global;application/vnd.sun.xml.writer.template;application/x-font-otf;application/vnd.yamaha.openscoreformat.osfpvg+xml;application/vnd.osgi.dp;application/vnd.palm;text/x-pascal;application/vnd.pawaafile;application/vnd.hp-pclxl;application/vnd.picsel;image/x-pcx;image/vnd.adobe.photoshop;application/pics-rules;image/x-pict;application/x-chat;aplication/pkcs10;application/x-pkcs12;application/pkcs7-mime;application/pkcs7-signature;application/x-pkcs7-certreqresp;application/x-pkcs7-certificates;application/pkcs8;application/vnd.pocketlearn;image/x-portable-anymap;image/-portable-bitmap;application/x-font-pcf;application/font-tdpfr;application/x-chess-pgn;image/x-portable-graymap;image/png;image/x-portable-pixmap;application/pskc+xml;application/vnd.ctc-posml;application/postscript;application/xfont-type1;application/vnd.powerbuilder6;application/pgp-encrypted;application/pgp-signature;application/vnd.previewsystems.box;application/vnd.pvi.ptid1;application/pls+xml;application/vnd.pg.format;application/vnd.pg.osasli;tex/prs.lines.tag;application/x-font-linux-psf;application/vnd.publishare-delta-tree;application/vnd.pmi.widget;application/vnd.quark.quarkxpress;application/vnd.epson.esf;application/vnd.epson.msf;application/vnd.epson.ssf;applicaton/vnd.epson.quickanime;application/vnd.intu.qfx;video/quicktime;application/x-rar-compressed;audio/x-pn-realaudio;audio/x-pn-realaudio-plugin;application/rsd+xml;application/vnd.rn-realmedia;application/vnd.realvnc.bed;applicatin/vnd.recordare.musicxml;application/vnd.recordare.musicxml+xml;application/relax-ng-compact-syntax;application/vnd.data-vision.rdz;application/rdf+xml;application/vnd.cloanto.rp9;application/vnd.jisp;application/rtf;text/richtex;application/vnd.route66.link66+xml;application/rss+xml;application/shf+xml;application/vnd.sailingtracker.track;image/svg+xml;application/vnd.sus-calendar;application/sru+xml;application/set-payment-initiation;application/set-reistration-initiation;application/vnd.sema;application/vnd.semd;application/vnd.semf;application/vnd.seemail;application/x-font-snf;application/scvp-vp-request;application/scvp-vp-response;application/scvp-cv-request;application/svp-cv-response;application/sdp;text/x-setext;video/x-sgi-movie;application/vnd.shana.informed.formdata;application/vnd.shana.informed.formtemplate;application/vnd.shana.informed.interchange;application/vnd.shana.informed.package;application/thraud+xml;application/x-shar;image/x-rgb;application/vnd.epson.salt;application/vnd.accpac.simply.aso;application/vnd.accpac.simply.imp;application/vnd.simtech-mindmapper;application/vnd.commonspace;application/vnd.ymaha.smaf-audio;application/vnd.smaf;application/vnd.yamaha.smaf-phrase;application/vnd.smart.teacher;application/vnd.svd;application/sparql-query;application/sparql-results+xml;application/srgs;application/srgs+xml;application/sml+xml;application/vnd.koan;text/sgml;application/vnd.stardivision.calc;application/vnd.stardivision.draw;application/vnd.stardivision.impress;application/vnd.stardivision.math;application/vnd.stardivision.writer;application/vnd.tardivision.writer-global;application/vnd.stepmania.stepchart;application/x-stuffit;application/x-stuffitx;application/vnd.solent.sdkm+xml;application/vnd.olpc-sugar;audio/basic;application/vnd.wqd;application/vnd.symbian.install;application/smil+xml;application/vnd.syncml+xml;application/vnd.syncml.dm+wbxml;application/vnd.syncml.dm+xml;application/x-sv4cpio;application/x-sv4crc;application/sbml+xml;text/tab-separated-values;image/tiff;application/vnd.to.intent-module-archive;application/x-tar;application/x-tcl;application/x-tex;application/x-tex-tfm;application/tei+xml;text/plain;application/vnd.spotfire.dxp;application/vnd.spotfire.sfs;application/timestamped-data;applicationvnd.trid.tpt;application/vnd.triscape.mxs;text/troff;application/vnd.trueapp;application/x-font-ttf;text/turtle;application/vnd.umajin;application/vnd.uoml+xml;application/vnd.unity;application/vnd.ufdl;text/uri-list;application/nd.uiq.theme;application/x-ustar;text/x-uuencode;text/x-vcalendar;text/x-vcard;application/x-cdlink;application/vnd.vsf;model/vrml;application/vnd.vcx;model/vnd.mts;model/vnd.vtu;application/vnd.visionary;video/vnd.vivo;applicatin/ccxml+xml,;application/voicexml+xml;application/x-wais-source;application/vnd.wap.wbxml;image/vnd.wap.wbmp;audio/x-wav;application/davmount+xml;application/x-font-woff;application/wspolicy+xml;image/webp;application/vnd.webturb;application/widget;application/winhlp;text/vnd.wap.wml;text/vnd.wap.wmlscript;application/vnd.wap.wmlscriptc;application/vnd.wordperfect;application/vnd.wt.stf;application/wsdl+xml;image/x-xbitmap;image/x-xpixmap;image/x-xwindowump;application/x-x509-ca-cert;application/x-xfig;application/xhtml+xml;application/xml;application/xcap-diff+xml;application/xenc+xml;application/patch-ops-error+xml;application/resource-lists+xml;application/rls-services+xml;aplication/resource-lists-diff+xml;application/xslt+xml;application/xop+xml;application/x-xpinstall;application/xspf+xml;application/vnd.mozilla.xul+xml;chemical/x-xyz;text/yaml;application/yang;application/yin+xml;application/vnd.ul;application/zip;application/vnd.handheld-entertainment+xml;application/vnd.zzazz.deck+xml");
                profile.setPreference("privacy.popups.showBrowserMessage", false);
                profile.setPreference("network.cookie.prefsMigrated", true);
                profile.setPreference("network.cookie.lifetimePolicy", 0);
                profile.setPreference("network.cookie.lifetime.days", 30);
                profile.setPreference("app.update.enabled", false);
                profile.setPreference("app.update.auto", false);


                capability.setCapability(FirefoxDriver.PROFILE, profile);
                driver =  new RemoteWebDriver(new URL("http://source.cloud.infomentum.co.uk:4441/wd/hub"), capability);
            }
            
            else {
                DesiredCapabilities capability = new DesiredCapabilities().firefox();
                capability.setCapability("build", "JUnit-Parallel");
                //capability.setCapability(FirefoxDriver.PROFILE, profile);
                driver =  new RemoteWebDriver(new URL("http://192.168.99.100:4444/wd/hub"), capability);
            }
        }
        
        // for creating a driver instance on a grid hub running on Jenkins machine
        

        
        // for running a browser session on browserstack
    
        else {
		System.out.println("else");
            // get the database connection details
            String browserStackUSERNAME = System.getProperty("BROWSERSTACK_USER");
            String browserStackAUTOMATE_KEY = System.getProperty("BROWSERSTACK_KEY");

            //String sauceLabsUSERNAME = propertyfile.getPropValues(propfile, "sauceLabsUSERNAME");
            //String sauceLabsAUTOMATE_KEY = propertyfile.getPropValues(propfile, "sauceLabsAUTOMATE_KEY");
            
            // If the browser type is not one of the above, intiate a new request to Browserstack remote testing
            // The BrowserType (e.g. Chrome), BrowserVersion (e.g. 27.0), OS type (e.g. Windows) and OS version should be provided as comma separated key value pairs
            // e.g. "os:Windows,os_version:10,browser:Chrome,browser_version:27.0"
            // full capability list see here
            // If not provided, default values will be used; Windows 10, Chrome 27.0
            
            BrowserStackHelper IMBrowserStackHelper = null;
            IMBrowserStackHelper = new BrowserStackHelper();

            SauceLabsHelper SauceLabsHelper = null;
            SauceLabsHelper = new SauceLabsHelper();
            
            String[] capsArray = browser.split(",");
            List<String> capabilities  = Arrays.asList(capsArray);            
                    
            driver = IMBrowserStackHelper.getRemoteBrowserSingle(capabilities, System.getProperty("jiraRef"),browserStackUSERNAME,browserStackAUTOMATE_KEY,System.getProperty("projectName"),System.getProperty("BUILD_NUMBER"));
            //driver = IMSauceLabsHelper.getRemoteBrowserSingle(capabilities, stepDefs.GetJiraRef(),sauceLabsUSERNAME,sauceLabsAUTOMATE_KEY,stepDefs.projectName,stepDefs.BUILD_NUMBER);
        }
        
        //open a new driver instance to our application URL
        //driver.manage().deleteAllCookies();
        //driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(appURL);
        this.js = (JavascriptExecutor) driver;
    }
    
    /////////////////////////////////////////
    //helper functionality
    /////////////////////////////////////////

    // turn off implicit waits

    public void turnOffImplicitWaits() {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    // turn on implicit waits

    public void turnOnImplicitWaits() {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
    
    // saves all cookies from the browser
    
    public Set<Cookie> getAllCookies() throws Exception {
        System.out.println("cookies before reopening browser: "+ driver.manage().getCookies().toString());
        allCookies = driver.manage().getCookies();
        return allCookies;
    }
    
    // loads a named cookie from a set of cookies
    
    public boolean setNamedCookie(String cookieName) throws Exception {
        boolean test = false;
        // And now output all the available cookies for the current URL
        for (Cookie loadedCookie : allCookies) {
         //   driver.manage().addCookie(loadedCookie);
            System.out.println(String.format("%s -> %s", loadedCookie.getName(), loadedCookie.getValue()));
            if(loadedCookie.getName().contains(cookieName)) {
                driver.manage().addCookie(loadedCookie);
                test = true;
            }
        }
        System.out.println("cookies after reopening browser: "+ driver.manage().getCookies().toString());
        return test;
    }
    
    // refresh the page
    
    public void refreshThePage() throws Exception {
        driver.navigate().refresh();
    }

    /////////////////////////////////////////
    //waiting functionality
    /////////////////////////////////////////

    //a method for allowing selenium to pause for a set amount of time

    public void wait(int seconds) throws InterruptedException {
        if (seconds > 0) {
            Thread.sleep(seconds * 1000);
        }
    }

    public void wait(double seconds) throws InterruptedException {
        seconds = seconds * 1000;
        int pause = (int) seconds;
        Thread.sleep(pause);
    }

    //a method for waiting until an element becomes present

    public boolean waitForElementPresent(Locators locator, String element,
                                      int timeout,
                                      double interval) throws Exception {
        //wait for up to XX seconds for our error message
        //will check at each YY interval
        turnOffImplicitWaits();
        long end = System.currentTimeMillis() + (timeout * 1000);
        boolean result = false;
        while (System.currentTimeMillis() < end) {

            try {
                getWebElementNoHandling(locator, element);
                System.out.println("element found");
                result=true;
                break;
            }

            catch (NoSuchElementException e) {
                System.out.println("element not found this time");
                wait(interval);
                System.out.println("waited");
            }
        }
        turnOnImplicitWaits();
        if (result==true) {
            getWebElement(locator, element);
        }
        else {
            throw new NoSuchElementException(locator + element + "Element not found within " + timeout);
        }
        return result;
    }
    
    // waits for the page url to change

    public void waitForUrlToChange(String currentUrl,
                                      int timeout, double interval) throws Exception {

        //wait for up to XX seconds for url to change
        turnOffImplicitWaits();
        long end = System.currentTimeMillis() + (timeout * 1000);
        String testUrl = currentUrl;
        while ((System.currentTimeMillis() < end)&&(testUrl.equals(currentUrl))) {
            testUrl=getPageUrl();
            wait(interval);
            System.out.println(testUrl);
        }
        turnOnImplicitWaits();
        // throw exception if url does not change
        if (testUrl.equals(currentUrl)) {
            throw new NoSuchElementException("URL not changed within " + timeout);
        }
    }
    
    // a method for waiting for one of multiple elements to become present
    
    public boolean waitForElementListPresent(HashMap<String, Locators> elements, int timeout, double interval) throws Exception {
        turnOffImplicitWaits();
        
        long end = System.currentTimeMillis() + (timeout * 1000);
        // get they keyset for the list
        Set<String> keyset = new HashSet<String>();
        // get the key set from the hashmap
        keyset=elements.keySet();  
        // get the key set from the hashmap
        Iterator elementIterator = keyset.iterator();
        boolean result = false;
        
        while ((System.currentTimeMillis() < end)&&result==false) {
            
            while(elementIterator.hasNext()) {
                Object key = elementIterator.next();
            
                try {
                    getWebElement(elements.get(key), key.toString());
                    System.out.println("found element" + elements.get(key).toString());
                    result=true;
                }

                catch (NoSuchElementException e) {
                    result=false;
                }
            }
            wait(interval);
        }        
        turnOnImplicitWaits();
        
        // throw exception if element list not found
        if (result==false) {
            throw new NoSuchElementException("Element list not found within " + timeout);
        }
        
        return result;
    }

    // A refactored wait for element displayed method

    public void waitForElementDisplayed(Locators locator, String element,
                                           int timeout,
                                           double interval) throws Exception {
        waitForElementDisplayed(getWebElement(locator, element), timeout,
                                   interval);
    }

    public void waitForElementDisplayed(WebElement element, int timeout,
                                           double interval) throws Exception {

        //wait for up to XX seconds for our error message
        //will check at each YY interval
        int seconds = timeout;
        boolean result = false;
        long end = System.currentTimeMillis() + (seconds * 1000);
        while (System.currentTimeMillis() < end) {
            if (element.isDisplayed() == true) {
                System.out.println("element displayed");
                result=true;
                break;
            } else {
                System.out.println("element not displayed this time");
                result=false;
                wait(interval);
            }
        }
        if (result==false) {
            throw new NoSuchElementException("Element not displayed within " + timeout);
        }
    }
    
    // wait for an attribute condition - element to be come clickable
   
    public void waitForElementClickable(Locators locator, String element, int timeout, double interval) throws Exception {
        turnOffImplicitWaits();
        WebElement clickableelement = getWebElement(locator, element);
        int seconds = timeout;
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        int count = 0;
        int maxTries = 5;
        while (true) {
            try {
                wait.until((ExpectedConditions.elementToBeClickable(clickableelement)));
                break;
            } catch (StaleElementReferenceException e) {
                wait(interval);
                clickableelement = getWebElement(locator, element);
                if (++count == maxTries)
                    throw e;
            }
        }
        turnOnImplicitWaits();
    }
    
    // TODO add more methods for wait for expected conditions

    //wait for text to become present in a specific element

    public void waitForElementTextPresent(Locators locator, String element,
                                          String text,
                                          int timeout, double interval) throws Exception {
        waitForElementTextPresent(element, timeout, text, locator, interval);
    }

    public void waitForElementTextPresent(String element, int seconds,
                                          String text,
                                          Locators locator, double interval) throws Exception {
        // If results have been returned, the results are displayed in a drop down.

        WebElement elements = getWebElement(locator, element);
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        int count = 0;
        int maxTries = 5;
        while (true) {
            try {
                System.out.println(text);
                System.out.println(getElementText(elements));
                wait.until((ExpectedConditions.textToBePresentInElement(elements,
                                                                        text)));
                break;
            } catch (StaleElementReferenceException e) {
                wait(interval);

                elements = getWebElement(locator, element);
                if (++count == maxTries)
                    throw e;
            }
        }
    }

    // wait for text to become present in a specific element

    public void waitForElementTextPresentNew(Locators locator, String element,
                                             String text, int timeout,
                                             double interval) throws Exception {
        // If results have been returned, the results are displayed in a drop down.
        WebElement elements = getWebElement(locator, element);
        //wait for up to XX seconds for our error message
        //will check at each YY interval
        long end = System.currentTimeMillis() + (timeout * 1000);
        while (System.currentTimeMillis() < end) {

            try {
                if (getElementText(locator, element).equals(text)) {
                    System.out.println(getElementText(locator, element));
                    break;
                } else {
                    System.out.println(getElementText(locator, element));
                    wait(interval);
                }
            }

            catch (StaleElementReferenceException s) {
                System.out.println("Stale element");
                wait(interval);
                elements = getWebElement(locator, element);
            }

            catch (NoSuchElementException e) {
                System.out.println("Element not found");
                wait(interval);
            }
        }
    }

    //wait for value to become present in a specific element

    public void waitForElementValuePresent(Locators locator, String element,
                                           String text, double interval) throws Exception {
        waitForElementValuePresent(element, 30, text, locator, interval);
    }
    
    // TODO this doesnt look correct
    
    public void waitForElementValuePresent(String element, int seconds,
                                           String text,
                                           Locators locator, double interval) throws Exception {
        // If results have been returned, the results are displayed in a drop down.
        //wait for up to XX seconds for our error message
        WebElement elements = getWebElement(locator, element);
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        wait.until((ExpectedConditions.stalenessOf(elements)));
    }

    // Waits for an element to become not visible

    public void waitForElementNotVisible(Locators locator,
                                         String element) throws Exception {
        waitForElementNotVisible(element, 8, locator);
    }

    public void waitForElementNotVisible(String element, int seconds,
                                         Locators locator) throws Exception {
        // If results have been returned, the results are displayed in a drop down.
        //wait for up to XX seconds for our error message
        long end = System.currentTimeMillis() + (seconds * 1000);
        while (System.currentTimeMillis() < end) {
            WebElement elements = getWebElement(locator, element);
            // If results have been returned, the results are displayed in a drop down.
            int count = 0;
            int maxTries = 5;
            while (true) {
                try {
                    if (elements.isDisplayed() == false) {
                        break;
                    }
                } catch (StaleElementReferenceException e) {
                    break;
                } catch (NoSuchElementException e) {
                    break;
                }
            }
        }
    }

    public boolean waitForElementClassPresent(Locators locator, String element,
                                           int timeout, double interval,
                                           String className) throws Exception {

        //wait for up to XX seconds for our error message
        //will check at each YY interval
        int seconds = timeout;
        long end = System.currentTimeMillis() + (seconds * 1000);
        boolean result = false;
        while (System.currentTimeMillis() < end) {

            if (getElementClass(locator, element).contains(className) ==
                true) {
                System.out.println("class found");
                result=true;
                break;
            } else {
                System.out.println("class not found this time");
                wait(interval);
            }
        }
        // throw exception if element list not found
        if (result==false) {
            throw new NoSuchElementException("Class not found within " + timeout);
        }
        return result;
    }

    // A method to wait for an element to not have a certain class

    public boolean waitForElementClassNotPresent(Locators locator, String element,
                                              int timeout, double interval,
                                              String className) throws Exception {
        //wait for up to XX seconds for our error message
        //will check at each YY interval
        int seconds = timeout;
        boolean result=false;
        long end = System.currentTimeMillis() + (seconds * 1000);
        int count = 0;
        int maxTries = 3;
        while (System.currentTimeMillis() < end) {
            try {
                if (getElementClass(locator, element).contains(className) ==
                    false) {
                    System.out.println("class now gone");
                    result=true;
                    break;
                } else {
                    System.out.println("class still found this time");
                    wait(interval);
                }
            } catch (NoSuchElementException e) {
                if (++count == maxTries)
                    throw e;
                wait(1);
            } catch (StaleElementReferenceException s) {
                if (++count == maxTries)
                    throw s;
                System.out.println("STALE");
                wait(1);
            }
        }
        // throw exception if element list not found
        if (result==false) {
            throw new NoSuchElementException("Class not gone within " + timeout);
        }
        return result;
    }
    
    // a method to wait for an element to contain an attribute
    
    public boolean waitForElementAttributePresent(Locators locator, String elementText,
                                              int timeout, double interval,
                                              String attribute) throws Exception {
        //wait for up to XX seconds for our error message
        //will check at each YY interval
        int seconds = timeout;
        turnOffImplicitWaits();
        long end = System.currentTimeMillis() + (seconds * 1000);
        int count = 0;
        int maxTries = 3;
        boolean result = false;
        while ((System.currentTimeMillis() < end) && (result == false)) {
            System.out.println("HERE");
            try {
                WebElement element = getWebElement(locator,elementText);
                String value = element.getAttribute(attribute);
                if (value != null){
                    System.out.println("attribute present");
                    result = true;
                }
                else {
                    result = false;
                    System.out.println("attribute not present");
                    wait(interval);
                }
            } catch (NoSuchElementException e) {
                if (++count == maxTries)
                    throw e;
                wait(1);
            } catch (StaleElementReferenceException s) {
                if (++count == maxTries)
                    throw s;
                System.out.println("STALE");
                wait(1);
            }
            Thread.sleep(100);
        }
        // throw exception if element list not found
        turnOnImplicitWaits();
        if (result==false) {
            throw new NoSuchElementException("Attribute not present within " + timeout);
        }
        return result;
    }
    
    // need to relocate the element on each iteration otherwise stale
    public boolean waitForElementAttributeNotPresent(Locators locator, String elementText,
                                              int timeout, double interval,
                                              String attribute) throws Exception {
        //wait for up to XX seconds for our error message
        //will check at each YY interval
        int seconds = timeout;
        turnOffImplicitWaits();
        long end = System.currentTimeMillis() + (seconds * 1000);
        int count = 0;
        int maxTries = 3;
        boolean result = false;
        while ((System.currentTimeMillis() < end) && (result == false)) {
            try {
                WebElement element = getWebElement(locator,elementText);
                String value = element.getAttribute(attribute);
                if (value == null){
                    System.out.println("attribute no present");
                    result = true;
                }
                else {
                    result = false;
                    System.out.println("attribute still present");
                    wait(interval);
                }
            } catch (NoSuchElementException e) {
                if (++count == maxTries)
                    throw e;
                wait(1);
            } catch (StaleElementReferenceException s) {
                if (++count == maxTries)
                    throw s;
                System.out.println("STALE");
                wait(1);
            }
            Thread.sleep(100);
        }
        turnOnImplicitWaits();
        // throw exception if element list not found
        if (result==false) {
            throw new NoSuchElementException("Attribute not removed within " + timeout);
        }
        return result;
    }

    // a method to wait for an element to not be present

    public boolean waitForElementNotPresent(Locators locator, String element,
                                         int timeout,
                                         double interval) throws Exception {

        //wait for up to XX seconds for our error message
        //will check at each YY interval
        int seconds = timeout;
        turnOffImplicitWaits();
        boolean result = false;
        long end = System.currentTimeMillis() + (seconds * 1000);
        while (System.currentTimeMillis() < end) {

            try {
                getWebElement(locator, element);
                System.out.println("element found this time");
                wait(interval);
            }

            catch (NoSuchElementException e) {
                System.out.println("element not found now");
                result=true;
                break;
            }
        }
        turnOnImplicitWaits();
        if (result==false) {
            throw new NoSuchElementException("Element not removed within " + timeout);
        }
        return result;
    }

    //////////////////////////////////////
    //checking element functionality
    //////////////////////////////////////

    //a method for checking id an element is displayed

    public boolean checkElementDisplayed(Locators locator, String element) throws Exception {

        if (checkElementDisplayed(getWebElement(locator, element)) == true) {
            return true;
        } 
        else {
            return false;
        }
    }

    public boolean checkElementDisplayed(WebElement element) throws Exception {
        if ((element.isDisplayed()) == true) {
            return true;
        } else {
            return false;
        }
    }
    
// a method to check if element is not Displayed with Locators
    public boolean checkElementNotDisplayed(Locators locator,String element) throws Exception {
        
        try{
            getWebElement(locator, element).isDisplayed();
                return false;
           }catch(org.openqa.selenium.NoSuchElementException e){
               return true;
           }
        } 
    
    // a method to check if element is not Displayed with WebElement
    public boolean checkWebElementNotDisplayed(WebElement element) throws Exception {

    if ((element.isDisplayed()) == true) {
            return false;
     } else {
             return true;
        }
    }
    //a method to check if Alert dialog is present

    public boolean isAlertPresent(){
        try 
        { 
            driver.switchTo().alert(); 
            return true; 
        }   
        catch (NoAlertPresentException Ex) 
        { 
            return false; 
        }  
    }

    public boolean waitForAlertPresent(int timeout, double interval) throws Exception {
        //wait for up to XX seconds for our error message
        //will check at each YY interval
        turnOffImplicitWaits();
        long end = System.currentTimeMillis() + (timeout * 1000);
        boolean result = false;
        while (System.currentTimeMillis() < end) {
            try
            {
                driver.switchTo().alert();
                System.out.println("alert found");
                result = true;

            }
            catch (NoAlertPresentException Ex) {
                System.out.println("Alert not found");
                result = false;
                wait(interval);
            }
        }
        turnOnImplicitWaits();
        if (result==true) {

        }
        else {
            throw new NoSuchElementException("Alart not found within " + timeout);
        }
        return result;
    }


    public void handleUploadEvent(String filepath, String elementid) throws Exception {

        //WebDriverWait wait = new WebDriverWait(driver, 10);
        //wait.until(ExpectedConditions.alertIsPresent());


        WebElement element = getWebElement(Locators.id,elementid);
        element.sendKeys(filepath);
        wait(5);
        System.out.println(element.getText());

//        // switch to the file upload window
//        Alert alert = driver.switchTo().alert();
//
//        // enter the filename
//        alert.sendKeys(filepath);
//
//        // hit enter
//        Robot r = new Robot();
//        r.keyPress(KeyEvent.VK_ENTER);
//        r.keyRelease(KeyEvent.VK_ENTER);
//
//        // switch back
//        driver.switchTo().activeElement();
        //sSystem.setProperty("java.awt.headless", "true");
//        wait(5);

//        System.out.println(filepath);
//        StringSelection s = new StringSelection(filepath);
//        //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
//        Robot robot = new Robot();
//        //robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
//        //robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
//        robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
//        robot.keyPress(java.awt.event.KeyEvent.VK_V);
//        robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
//        wait(5);
//        robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
//        wait(5);
//        robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
    }
    
//a method to check if an element is present
    
    public boolean checkElementPresent(Locators locator, String element) throws Exception {
        turnOffImplicitWaits();
        try {
            getWebElementNoHandling(locator, element);
            turnOnImplicitWaits();
            return true;
        }
        
        catch (MoveTargetOutOfBoundsException b) {
            turnOnImplicitWaits();
            return true;
        }

        catch (NoSuchElementException e) {
            turnOnImplicitWaits();
            return false;
        }
        
        catch (StaleElementReferenceException s) {
            turnOnImplicitWaits();
            return true;
        }
    }
    
    //a method to check if an element is present
    
    public boolean checkElementClickable(Locators locator, String element) throws Exception {
        turnOffImplicitWaits();
        try {
            getWebElement(locator, element);
            click(getWebElement(locator, element));
            turnOnImplicitWaits();
            return true;
        }
        
        catch (MoveTargetOutOfBoundsException b) {
            turnOnImplicitWaits();
            System.out.println("HERE");
            return false;
        }

        catch (NoSuchElementException e) {
            turnOnImplicitWaits();
            return false;
        }
        
        catch (StaleElementReferenceException s) {
            turnOnImplicitWaits();
            return false;
        }
    }
    
    // a method to check the text value of a given element

    public boolean checkElementText(Locators locator, String element, String intendedText) throws Exception {
        String actualElementText = "";
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                actualElementText = getElementText(locator, element);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        if (intendedText.equals(actualElementText)) {
            return true;
        } else {
            return false;
        }
    }
    
    // check if an element has a named attribute
    
    public boolean checkAttributeValuePresent(Locators locator, String elementText, String attribute, String intendedValue) throws Exception {
        System.out.println("checking the value");
        Boolean result = false;
        WebElement element = getWebElement(locator, elementText);
        try {
            String value = element.getAttribute(attribute);
            System.out.println("value: " + value + " "  + intendedValue);
            if ((value != null) && (value.contains(intendedValue))){
                result = true;
            }
        } catch (Exception e) {
            result = false;    
        }

        return result;
    }
    
    public boolean checkAttributeValuePresent(WebElement element, String attribute, String intendedValue) throws Exception {
        System.out.println("checking the value");
        Boolean result = false;
       
        try {
            String value = element.getAttribute(attribute);
            System.out.println("value: " + value + " "  + intendedValue);
            if ((value != null) && (value.contains(intendedValue))){
                result = true;
            }
        } catch (Exception e) {
            result = false;    
        }

        return result;
    }
    
    // check if an element is enabled
    
    public boolean checkElementEnabled(Locators locator, String elementText) throws Exception {
        
        Boolean result = false;
        WebElement element = getWebElement(locator, elementText);
        
        try {
            if (element.isEnabled()) {
                result = true;
            }
            else {
                result = false;
            }
        } catch (Exception e) {
            result = false;    
        }

        return result;
    }
    
    // check if an element has a named attribute
    
    public boolean checkAttributePresent(Locators locator, String element, String attribute) throws Exception {
        
        WebElement webelement = getWebElement(locator, element);
        return checkAttributePresent(webelement, attribute);
    }
    
    public boolean checkAttributePresent(WebElement element, String attribute) throws Exception {
        Boolean result = false;
        try {
            String value = element.getAttribute(attribute);
            if (value != null){
                result = true;
            }
        } catch (Exception e) {}

        return result;
    }
    
    // check element class present
    
    public boolean checkElementClassPresent(Locators locator, String element, String className) throws Exception {

        WebElement webelement = getWebElement(locator, element);
        return checkElementClassPresent(webelement, className);
    }
    
    public boolean checkElementClassPresent(WebElement element, String className) throws Exception {

        //wait for up to XX seconds for our error message
        //will check at each YY interval
        boolean result = false;

        if (getElementClass(element).contains(className) == true) {
            System.out.println("class found");
            result=true;
        } 
        else {
            result = false;
        }

        return result;
    }
    
    //////////////////////////////////////
    //selenium get functionality
    //////////////////////////////////////

    //get the page title

    public String getPageTitle() throws Exception {
        String pageTitle = driver.getTitle();
        return pageTitle;
    }

    //get the page url

    public String getPageUrl() throws Exception {
        String pageUrl = driver.getCurrentUrl();
        return pageUrl;
    }
    
    //get the current domain

    public String getPageDomain() throws Exception {
        String pageUrl = getPageUrl();
        int stringStart = pageUrl.indexOf("://") + 4;
        int stringFinish = pageUrl.indexOf("/", stringStart);
        String domain = pageUrl.substring(0, stringFinish);
        return domain;
    }

    //get the text of an element

    public String getElementText(Locators locator,
                                 String element) throws Exception {
        String elementText;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                elementText = getElementText(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return elementText;
    }

    public String getElementText(WebElement element) {
        String elementText = element.getText();
        return elementText;
    }
    
    // get element innerHTML
    
    public String getElementInnerHTML(Locators locator, String element) throws Exception {
        String elementInnerHTML;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                elementInnerHTML = getElementText(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return elementInnerHTML;
    }
    
    public String getElementInnerHTML(WebElement element) {
        String elementInnerHTML = element.getAttribute("innerHTML");
        return elementInnerHTML;
    }
    
    // get element outerHTML
    public String getElementOuterHTML(Locators locator, String element) throws Exception {
        String elementOuterHTML;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                elementOuterHTML = getElementOuterHTML(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return elementOuterHTML;
    }
    
    public String getElementOuterHTML(WebElement element) {
        String elementOuterHTML = element.getAttribute("outerHTML");
        return elementOuterHTML;
    }

    // get element tagname
    public String getElementTagName(Locators locator, String element) throws Exception {
        String elementTagName;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                elementTagName = getElementTagName(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return elementTagName;
    }

    public String getElementTagName(WebElement element) {
        String elementTagName = element.getTagName();
        return elementTagName;
    }


    // get a list of options from a select box

    public List<WebElement> selectOptionList(Locators locator,
                                             String element) throws Exception {
        List<WebElement> selectOptionList;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                selectOptionList =
                        selectOptionList(getWebElement(locator, element));
                break;

            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return selectOptionList;
    }

    public List<WebElement> selectOptionList(WebElement element) throws Exception {
        List<WebElement> selectOptionList = new Select(element).getOptions();
        return selectOptionList;
    }
    
    //select by visible text 

    public void selectOptionbyVisibleText(WebElement element,String text) throws Exception {
        Select selectOptionList = new Select(element);
        selectOptionList.selectByVisibleText(text);
    }
    
    // get the number of options from a select box

    public int selectOptionSize(Locators locator,
                                String element) throws Exception {
        int selectOptionListSize;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                selectOptionListSize =
                        selectOptionSize(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return selectOptionListSize;
    }
       
    public int selectOptionSize(WebElement element) throws Exception {
        List<WebElement> selectOptionList = new Select(element).getOptions();
        int selectOptionListSize = selectOptionList.size();
        return selectOptionListSize;
    }

    // get drop down selected value

    public String getSelectValueText(Locators locator,
                                     String element) throws Exception {
        String selectedValueText;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                selectedValueText =
                        getSelectValueText(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return selectedValueText;
    }

    public String getSelectValueText(WebElement element) throws Exception {
        WebElement selectedOption =
            new Select(element).getFirstSelectedOption();
        String selectedValueText = getElementText(selectedOption);
        return selectedValueText;
    }

    //get the value of an element

    public String getElementValue(Locators locator,
                                  String element) throws Exception {
        String elementValue;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                elementValue =
                        getElementValue(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return elementValue;
    }

    public String getElementValue(WebElement element) {
        String elementValue = element.getAttribute("value");
        return elementValue;
    }

    // get the list of classes for an element

    public String getElementClass(Locators locator,
                                  String element) throws Exception {
        String elementClass;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                elementClass =
                        getElementClass(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return elementClass;
    }

    public String getElementClass(WebElement element) {
        String elementClass = element.getAttribute("class");
        return elementClass;
    }

    // get attribute value of an element

    public String getAttributeValue(Locators locator, String element,
                                    String attribute) throws Exception {
        String attributeValue;
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                attributeValue =
                        getAttributeValue(getWebElement(locator, element),
                                          attribute);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
        return attributeValue;
    }

    public String getAttributeValue(WebElement element, String attribute) {
        String attributeValue = element.getAttribute(attribute);
        System.out.println("attributeValue"+ attributeValue + " "+ attribute);
        return attributeValue;
    }

    // count the number of instances of an element

    public int countElement(Locators locator,
                            String element) throws Exception {
        int locatorElementSize = getWebElements(locator, element).size();
        return locatorElementSize;
    }

    // Highlight an element, use when debugging locators

    public void highlight(Locators locator, String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                highlight(getWebElement(locator, element));
                scrollToElement(locator, element);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }
    
    // copy to clipboard
    
    public void copyToClipBoard(Locators locator, String element) throws Exception {
        WebElement textBox = getWebElement(locator,element);
        System.out.println(getElementValue(textBox));
        textBox.sendKeys(Keys.LEFT_CONTROL + "a");
        textBox.sendKeys(Keys.LEFT_CONTROL + "c");
    }

    public void highlight(WebElement element) throws Exception {
        for (int i = 0; i < 1; i++) {
            JavascriptExecutor js = (JavascriptExecutor)driver;
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);",
                             element,
                             "color: red; border: 3px solid black;");

        }
    }

    /////////////////////////////////////
    //selenium actions functionality
    /////////////////////////////////////

    //locate an element and click it

    public void click(Locators locator, String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                click(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            } catch (MoveTargetOutOfBoundsException b) {
                counter = handleStaleElementException(b, counter);
            }
        }
    }

    public void click(WebElement element) {
        Actions selAction = new Actions(driver);
        selAction.click(element).perform();
    }
    
    // a method to test if a field is present

    public boolean clickTest(WebElement element) {
        Actions selAction = new Actions(driver);
        try {
            //grab our element based on the locator
            
            selAction.click(element).perform();
            selAction.click(element).perform();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        } catch (InvalidElementStateException l) {
                return false;
        } catch (StaleElementReferenceException s) {
            return false;
        } catch (MoveTargetOutOfBoundsException b) {
            return false;
        }
    }
    
    // a method to test if a field is present

    public boolean typeTest(WebElement element) throws  Exception {
        
        try {
            //grab our element based on the locator
            
            type(element,"");
            return true;
        } catch (NoSuchElementException e) {
            return false;
        } catch (InvalidElementStateException l) {
                return false;
        } catch (StaleElementReferenceException s) {
            return false;
        } catch (MoveTargetOutOfBoundsException b) {
            return false;
        }
    }
    
    //locate an element and click it

    public void scrollToElement(Locators locator, String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                scrollToElement(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            } catch (MoveTargetOutOfBoundsException b) {
                counter = handleStaleElementException(b, counter);
            }
        }
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", element);
    }
    
    //locate an element and double click it

    public void doubleClick(Locators locator,
                            String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                doubleClick(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void doubleClick(WebElement element) {
        Actions selAction = new Actions(driver);
        selAction.doubleClick(element).perform();
    }

    //locate an element and click and hold

    public void clickAndHold(Locators locator,
                             String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                clickAndHold(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void clickAndHold(WebElement element) {
        Actions selAction = new Actions(driver);
        selAction.clickAndHold(element).perform();
    }

    //locate an element and right click it

    public void rightClick(Locators locator, String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                rightClick(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void rightClick(WebElement element) {
        Actions selAction = new Actions(driver);
        selAction.contextClick(element).perform();
    }

    //a method to simulate the mouse hovering over an element

    public void hover(Locators locator, String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                hover(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void hover(WebElement element) throws Exception {
        Actions selAction = new Actions(driver);
        selAction.moveToElement(element).perform();
    }

    //a method hover on an element and select an element in the submenu

    public void hoverAndClick(Locators hoverlocator, String hoverelement,
                              Locators clicklocator,
                              String clickelement) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                hoverAndClick((getWebElement(hoverlocator, hoverelement)),
                              clicklocator, clickelement);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    // a method for operating a mega menu
    // TODO not working for Motability but fine for BAE

    public void hoverAndClick(WebElement hoverelement, Locators clicklocator,
                              String clickelement) throws Exception {
        Actions selAction = new Actions(driver);
        // hover the first element
        selAction.moveToElement(hoverelement).build().perform();
        // wait for the second slement to become clickable
        waitForElementClickable(clicklocator, clickelement,10,0.5);
        WebElement clickElement = getWebElement(clicklocator, clickelement);
        selAction.moveToElement(clickElement).build().perform();
        click(clicklocator, clickelement);
    }

    // select an item from a drop down

    public void selectByText(Locators locator, String element, String selection) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                selectByText(getWebElement(locator, element), selection);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void selectByText(WebElement element,
                             String selection) throws Exception {
        new Select(element).selectByVisibleText(selection);
        System.out.println("SELECCTED" + selection);
    }
    
    // select by text non case sensitve
    
    public void selectByTextNonCaseSensitive(Locators locator, String element, String selection) throws Exception {
    Select dropDown = new Select(getWebElement(locator,element));
    int index = 0;
        for (WebElement option : dropDown.getOptions()) {
            if (option.getText().equalsIgnoreCase(selection))
                break;
            index++;
        }
        dropDown.selectByIndex(index);
    }

    // select an item from a drop down

    public void selectByIndex(Locators locator, String element, int index) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                selectByIndex(getWebElement(locator, element), index);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void selectByIndex(WebElement element, int index) throws Exception {
        new Select(element).selectByIndex(index);
    }
    
    // select an item from a drop down

    public void selectByIndexRandom(Locators locator, String element, int index) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                selectByIndexRandom(getWebElement(locator, element), index);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void selectByIndexRandom(WebElement element, int index) throws Exception {
        new Select(element).selectByIndex(randomNumberBetweenTwoPoints(1,selectOptionSize(element)));
    }

    // select an item from a drop down

    public void selectByValue(Locators locator, String element,
                              String value) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                selectByValue(getWebElement(locator, element), value);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void selectByValue(WebElement element,
                              String value) throws Exception {
        new Select(element).selectByValue(value);
    }

    //our generic selenium type functionality

    public void type(Locators locator, String element,
                     String text) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                type(getWebElement(locator, element), text);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void type(WebElement element, String text) throws Exception {
        element.clear();
        Actions selAction = new Actions(driver);
        selAction.sendKeys(element, text).perform();
    }

    public void moveFocus() throws Exception {
        Actions selAction = new Actions(driver);
        selAction.sendKeys(Keys.TAB).perform();
        Thread.sleep(500);
    }
    
    // use for a drop down box
    public void sendDownKeystroke(WebElement element,int i) {
        element.clear();
        Actions selAction = new Actions(driver);
        for(int j=0; j<=i;j++){
        selAction.sendKeys(element, Keys.ARROW_DOWN).perform();
        }
    }
    
    //send a generic down key to move the page down 
    public void pressDownKeystroke(int i) {
        Actions selAction = new Actions(driver);
        selAction.sendKeys(Keys.ARROW_DOWN).perform();
    }

    // a method that sends the return key to an element

    public void pressReturn(Locators locator,
                            String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                pressReturn(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void pressReturn(WebElement element) {
        element.sendKeys(Keys.RETURN);
    }
    
    // Drag an element to another element


    public void dragToElement(Locators locator, String element,
                              String target) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                dragToElement((getWebElement(locator, element)),
                              (getWebElement(locator, target)));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    public void dragToElement(WebElement element, WebElement target) {
        Actions selAction = new Actions(driver);
        selAction.dragAndDrop(element, target);
    }

    // Drag and drop an element by an index

    public void dragByIndex(Locators locator, String element, int vertical,
                            int horizontal) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                dragByIndex(getWebElement(locator, element), vertical,
                            horizontal);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    ////////////////////////////////////
    //window and frame handling
    ////////////////////////////////////

    public void dragByIndex(WebElement element, int vertical, int horizontal) {
        Actions selAction = new Actions(driver);
        selAction.dragAndDropBy(element, vertical, horizontal).perform();
    }
    
    // Load a different url same window

    public void loadPageSameWindow(String url) {
        driver.get(url);
    }
    
    // Load a different url same window relative path

    public void loadPageRelativePathSameWindow(String path) throws Exception {
        driver.get(getPageDomain()+path);
    }

    // Load a different url new window

    public void loadPageNewWindow(String url) {
        driverTwo = new FirefoxDriver();
        driverTwo.get(url);
    }
    
    // close extra window
    
    public void closeExtraWindow() {
        driverTwo.quit();
    }

    // Switch frame - select a frame based on a locator

    public void switchFrame(Locators locator,
                            String element) throws Exception {
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                switchFrame(getWebElement(locator, element));
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            }
        }
    }

    // switch to a different frame
    
    public void switchFrame(WebElement element) {
        driver.switchTo().frame(element);
    }

    // Revert to the main frame

    public void selectMainFrame() throws Exception {
        driver.switchTo().defaultContent();
    }

    public String getCurrentWindowHanlde() throws Exception {
        //Store the current window handle
        String winHandle = driver.getWindowHandle();
        //Perform the click operation that opens new window
        return winHandle;
    }

    public void selectNewWindow() throws Exception {
        //Switch to new window opened
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }
    
    // for counting the number of open windows
    
    public int countWindowHandles() throws Exception {
        
        // wait for the new window
       return driver.getWindowHandles().size();
    }

    public void selectWindowByHandle(String windHandle) throws Exception {
        driver.switchTo().window(windHandle);
    }

    // a method that closes the current window

    public void closeCurrentWindow() throws Exception {
        driver.close();
    }
    
        
    ////////////////////////////////////
    //extra base selenium functionality
    ////////////////////////////////////

    //a method to grab the web element using selenium webdriver
    
    public WebElement getWebElementNoHandling(Locators locator, String element) throws Exception {
        By byElement=setbyElement(locator,element);
        WebElement query;

        query = driver.findElement(byElement);
              
        // This will return the Element in case of no Exception
        return query; //return our query
    }
    
    public WebElement getWebElement(Locators locator, String element) throws Exception {
        By byElement=setbyElement(locator,element);
        WebElement query;
        
        int counter = 1;
        while (true) {
            try {
                //grab our element based on the locator
                query = driver.findElement(byElement);
                break;
            } catch (NoSuchElementException e) {
                counter = handleElementNotFoundException(e, counter);
            } catch (StaleElementReferenceException s) {
                counter = handleStaleElementException(s, counter);
            } catch (MoveTargetOutOfBoundsException b) {
                counter = handleStaleElementException(b, counter);
            }
        }
        
        // This will return the Element in case of no Exception
        return query; //return our query
    }
    
    // method to set the By locator
    
    private By setbyElement(Locators locator, String element) throws Exception {
        By byElement;
        switch (locator) { //determine which locator item we are interested in
        case xpath:
            {
                byElement = By.xpath(element);
                break;
            }
        case id:
            {
                byElement = By.id(element);
                break;
            }
        case partialid:
            {
                byElement = By.xpath("//*[contains(@id, '" + element + "')]");
                break;
            }
        case name:
            {
                byElement = By.name(element);
                break;
            }
        case partialname:
            {
                byElement =
                        By.xpath("//*[contains(@name, '" + element + "')]");
                break;
            }
        case classname:
            {
                byElement = By.className(element);
                break;
            }
        case partialclass:
            {
                byElement =
                        By.xpath("//*[contains(@class, '" + element + "')]");
                break;
            }
        case linktext:
            {
                byElement = By.linkText(element);
                break;
            }
        case paritallinktext:
            {
                byElement = By.partialLinkText(element);
                break;
            }
        case partialLinkDestination:
            {
                byElement =
                        By.xpath("//*[contains(@href, '" + element + "')]");
                break;
            }
            // find all elements by a given html tag - unlikely to resolve to a single environment
        case tagname:
            {
                byElement = By.tagName(element);
                break;
            }
            // element must be provided as "attributeName='attributeValue'"
            // e.g. celltype='celltype']"))
        case attribute:
            {
                byElement = By.xpath("//*[@" + element + "]");
                break;
            }
        case partialAttribute:
            {
                byElement = By.xpath("//*[contains(@" + element + "]");
                break;
            }
        case cssSelector:
            {
                byElement = By.cssSelector(element);
                break;
            }
        default:
            {
                throw new Exception();
            }
        }
        return byElement;
    }

    //a method to grab the web element using selenium webdriver

    public List<WebElement> getWebElements(Locators locator,
                                           String element) throws Exception {
        // get the byElement
        By byElement=setbyElement(locator,element);
        // get the web elements
        List<WebElement> elementList =
            driver.findElements(byElement); //grab our element based on the locator
        return elementList; //return our query
    }
       
    // a method to add to an existing list of elements

    public List<WebElement> addWebElements(List<WebElement> existingList, Locators locator,
                                           String element) throws Exception {
        By byElement=setbyElement(locator,element);
        List<WebElement> elementList =
            driver.findElements(byElement); //grab our element based on the locator
        int counter = 0;
        while (counter<elementList.size()) {
            existingList.add(elementList.get(counter));
            counter++;
        }
        return existingList; //return our query
    }
    
    // a method to obtain child elements of an element
    
    public List<WebElement> getChildElements(WebElement element) {
        List<WebElement> childs = element.findElements(By.xpath(".//*"));
        return childs;
    }
    
    // a method to obtain the parent element for a given element
    
    public WebElement getParentElement(WebElement element) {
        WebElement parent = element.findElement(By.xpath("parent::*"));
        return parent;
    }
    
    // a method to obtain the first child element
    
    public WebElement getFirstChildElement(WebElement element) {
        List<WebElement> childs = element.findElements(By.xpath(".//*"));
        return childs.get(0);
    }
    
    // get the next sibling for an element
    
    public WebElement getSiblingElement(WebElement element, String text) {
        WebElement sibling = element.findElement(By.xpath("following-sibling" + text));
        return sibling;
    }
    
    // get the precediing sibling for an element
    
    public WebElement getPreviousSiblingElement(WebElement element, String text) {
        WebElement sibling = element.findElement(By.xpath("preceding-sibling" + text));
        return sibling;
    }
    
    public WebElement getWebElementfromElementCss(WebElement element,String css){
        WebElement webelement = element.findElement(By.cssSelector(css));
        return webelement;
    }
    
    public List<WebElement> getChildElementsCss(WebElement element,String css){
        List<WebElement> childs = element.findElements(By.cssSelector(css));
        return childs;
    }
    // a method to obtain child elements matching a certain xpath
    
    public List<WebElement> getChildElementsXpath(WebElement element, String xpath) {
        List<WebElement> childs = element.findElements(By.xpath(xpath));
        return childs;
    }

    //a method to obtain screenshots as files

    public String takeScreenshot(String filename) throws IOException {
        //make our screenshot name friendly
        filename = filename.replaceAll("[^a-zA-Z0-9/]", "");

        //take a screenshot
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere
        FileUtils.copyFile(scrFile, new File("target/" + filename + ".png"));
        return "target/" + filename + ".png";
    }

    //a method to obtain screenshots as files

    public String takeScreenshotFullScreen(String filename) throws IOException {
        //take a screenshot

        //take the screenshot of the entire home page and save it to a png file
        Screenshot ashot = new AShot()
                .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                .takeScreenshot(driver);

        File file = new File("target/"+filename);
        //file.getParentFile().mkdirs();
        ImageIO.write(ashot.getImage(), "PNG", file);
        //Path path = Paths.get("target/"+filename);

        // Now you can do whatever you need to do with it, for example copy somewhere
        return filename;
    }

    //a method to obtain screenshots as files

    public final byte[] takeScreenshotAsByte() throws IOException {
        //take a screenshot
        final byte[] screenshot =((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        // Now you can do whatever you need to do with it, for example copy somewhere
        return screenshot;
    }
    
    //a method to obtain screenshots as files
  
    public final byte[] takeScreenshotFullScreenAsByte() throws IOException {
        //take a screenshot
        
        //take the screenshot of the entire home page and save it to a png file
        Screenshot ashot = new AShot()
            .shootingStrategy(ShootingStrategies.viewportPasting(1000))
            .takeScreenshot(driver);

        File file = new File("temp");
        //file.getParentFile().mkdirs();
        ImageIO.write(ashot.getImage(), "PNG", file);
        Path path = Paths.get("temp");

        final byte[] screenshot = Files.readAllBytes(path);
        file.delete();
        // Now you can do whatever you need to do with it, for example copy somewhere
        return screenshot;
    }
    
    // a method to take a certain part of the screen
    
    public String takeScreenshotOfElement(Locators locator, String element, String filename) throws Exception {
        WebElement webElement = getWebElement(locator,element);
        
        //make our screenshot name friendly
        filename = filename.replaceAll("[^a-zA-Z0-9/]", "");

        //take a screenshot
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere
        FileUtils.copyFile(scrFile, new File("target/" + filename + ".png"));
        
        BufferedImage image = ImageIO.read(scrFile);
        Point point = webElement.getLocation();
        BufferedImage elementImage = image.getSubimage(
              point.getX(), point.getY(), 
              webElement.getSize().getWidth(), webElement.getSize().getHeight());
        ImageIO.write(elementImage, "png", scrFile);
        //Copy the element screenshot to disk
        File screenshotLocation = new File("target/" + filename + ".png");
        FileUtils.copyFile(scrFile, screenshotLocation);
        
        return "target/" + filename + ".png";
    }
    
    // screenshot entire screen
    
    public void screenshotEntireScreen(String filename) throws Exception {
        //take the screenshot of the entire home page and save it to a png file
        Screenshot ashot = new AShot()
            .shootingStrategy(ShootingStrategies.viewportPasting(1000))
            .takeScreenshot(driver);

        File file = new File(filename);
        file.getParentFile().mkdirs();
        ImageIO.write(ashot.getImage(), "PNG", file);
    }
    
    // a shot an element
    
    public void ashotElement(String filename, Locators locator, String element) throws Exception {
        //take the screenshot of a div element that includes all results page details>b
        Screenshot ashot = new AShot().takeScreenshot(driver, (getWebElement(locator,element)));
        ImageIO.write(ashot.getImage(), "PNG", new File(filename));
    }    
    
    // screenshot entire screen
    
    public Screenshot ashotPage() throws Exception {
        //take the screenshot of the entire home page and save it to a png file
        Screenshot ashot = new AShot()
            .shootingStrategy(ShootingStrategies.viewportPasting(1000))
            .takeScreenshot(driver);

        return ashot;
    }    
    
    //Take Screenshot with AShot
    public Screenshot takeElementAshot(WebElement element) {
        //Take screenshot with Ashot
        //AShot JQuery screenshot capture is not working. Thus, I used webdriver's CoordsProvider method.
        //Screenshot firstPhotoScreenshot = new AShot().takeScreenshot(driver, first_photo);
        Screenshot elementScreenShot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(driver,element);
    
        //Print element size
        String size = "Height: " + elementScreenShot.getImage().getHeight() + "\n" +
                      "Width: " + elementScreenShot.getImage().getWidth() + "\n";
        System.out.print("Size: " + size);
    
        return elementScreenShot;
    }
    
    // a method to write some interesting output to a csv file

    public void outputFile(String filename,
                           List<String> messages) throws Exception {
        java.util.Date date = new java.util.Date();
        FileWriter fw =
            new FileWriter("target//" + filename + "-" + (Thread.currentThread().getName()) +
                           ".csv", true);
        PrintWriter pw = new PrintWriter(fw, true);
        // put the url in the first column
        pw.print(driver.getCurrentUrl().replace(",", ""));
        pw.print(",");
        // put a timestamp in the second column
        pw.print(new Timestamp(date.getTime()));
        pw.print(",");
        // add data in additional columns as provided in the list
        int i = 0;
        while (i < messages.size()) {
            pw.print(messages.get(i).replace(",", " "));
            pw.print(",");
            i++;
        }
        pw.println();
        //Flush the output to the file
        pw.flush();
        //Close the Print Writer
        pw.close();
        //Close the File Writer
        fw.close();
    }
    
    // a method to count the lines in a csv
    
    public static int countLines(String filename) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
    
    // a method to send data of interest to analytics

    public void analytics(trackType track, String ua, List<String> elements) throws Exception {
        String gaUrl = "";
        if (ua.equals("")) {
            ua = "UA-48783302-1";
        }
        switch (track) { //determine which locator item we are interested in
        case event:
            {
                gaUrl =
                        ("http://www.google-analytics.com/collect?v=1&tid=" + ua +
                         "&cid=1111&t=event&ec=" + elements.get(0) + "&ea=" +
                         elements.get(1) + "&el=" + elements.get(2) + "&ev=" +
                         elements.get(3));
                break;
            }
        case timing:
            {
                gaUrl =
                        ("http://www.google-analytics.com/collect?v=1&tid=" + ua +
                         "&cid=1111&t=timing&utc=" + elements.get(0) +
                         "&utv=" + elements.get(1) + "&utt=" +
                         elements.get(2) + "&utl=" + elements.get(3));
                break;
            }
        case pageview:
            {
                break;
            }
        case social:
            {

                break;
            }
        case transaction:
            {

                break;
            }
        case item:
            {

                break;
            }
        case exception:
            {

                break;
            }

        default:
            {
                throw new Exception();
            }
        }

        String hostname = InetAddress.getLocalHost().getHostName();
        if (hostname.contains("source")) {

            System.setProperty("webdriver.chrome.driver","Drivers/chromedriver.exe");

            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);

            DesiredCapabilities capability = new DesiredCapabilities().chrome();
            capability.setCapability("build", "JUnit-Parallel");
            capability.setCapability("name", "Parallel test");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("test-type");
            options.addArguments("disable-popup-blocking");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-extensions");
            options.setExperimentalOption("prefs", chromePrefs);
            capability.setCapability(ChromeOptions.CAPABILITY, options);
            capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            driverTwo =  new RemoteWebDriver(new URL("http://source.cloud.infomentum.co.uk:4441/wd/hub"), capability);
            driverTwo.get(gaUrl);
            driverTwo.quit();
        }
        
        else {
            // dont do
        }
    }


    public int handleStaleElementException(Exception s,
                                           int count) throws Exception {
        if (++count == maxTries) {
            errorMessage = s.getMessage();
            StringWriter errors = new StringWriter();
            s.printStackTrace(new PrintWriter(errors));
            stackTrace = errors.toString();
            throw s;
        }
        else {
        System.out.println("Element stale");
        wait(1);
        }
        return count;
    }

    public int handleElementNotFoundException(Exception e,
                                              int count) throws Exception {
        if (++count == maxTries) {
            errorMessage = e.getMessage();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            stackTrace = errors.toString();
            throw e;
        }
        else {
        System.out.println("Element not found");
        wait(1);
        }
        return count;
    }
    
    // generate a random number between two points
    
    public int randomNumberBetweenTwoPoints(int min, int max) {
        if(max==0) {
            return 0;
        }
        else {
            Random r = new Random();
            int random = r.nextInt((max) - min) + min;
            return random;
        }
    }
    
    // generate a random number of certain length
    
    public String randomStringOfLength(int length, String stringType) {
        
        String number = "";
        
        if(length==0) {
            return "0";
        }
        
        else {
            
            if (stringType.equals("Numeric")) {
            
                int counter = 0;
                while (counter < length) {
                    Random r = new Random();
                    int random = r.nextInt((9) - 1) + 1;
                    number = number + random;
                    counter++;
                }
                
                System.out.println(number);
                return number;
        }
            
            else if (stringType.equals("Alphanumeric")) {
                if (length >= 18)  {
                    number = "JJD";
                    int counter = 0;
                    while (counter < (length-3)) {
                        Random r = new Random();
                        int random = r.nextInt((9) - 1) + 1;
                        number = number + random;
                        counter++;
                    }
                }
                
                else {
                    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                    StringBuilder salt = new StringBuilder();
                    Random rnd = new Random();
                    while (salt.length() < length) {
                        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                        salt.append(SALTCHARS.charAt(index));
                    }
                    number = salt.toString();
                    return number;
                }
                return number;                 
            }
            
            else if (stringType.equals("Letters")) {
                String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                StringBuilder salt = new StringBuilder();
                Random rnd = new Random();
                while (salt.length() < length) {
                    int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                    salt.append(SALTCHARS.charAt(index));
                }
                number = salt.toString();
                return number;
            }
        
            else {
                return "0";
            }
            
        }
    }
    
    // generate a timestamp as a string
    public String getTimestamp() throws Exception {
        java.util.Date date = new java.util.Date();
        // create a timestamp as a string
        String todaysdate = new Timestamp(date.getTime()).toString();
        // remove unwanted characters
        todaysdate = todaysdate.replace(".", "");
        todaysdate = todaysdate.replace(":", "");
        todaysdate = todaysdate.replace(" ", "-");
        return todaysdate;
    }
    
    // string similarity
    
    public static double similarity(String a, String b, String sep) {
      double count = 0;
      String[] words = b.split(sep);
      for(String word : words) {
        if((a.indexOf(word) != -1)||(word.indexOf(a) != -1)) {
          count++;
        }
      }
      
      System.out.println(count);
      
      return count / words.length;
    }
    
    // local storage methods
    
      public void removeItemFromLocalStorage(String item) {
        js.executeScript(String.format(
            "window.localStorage.removeItem('%s');", item));
      }
     
      public boolean isItemPresentInLocalStorage(String item) {
        return !(js.executeScript(String.format(
            "return window.localStorage.getItem('%s');", item)) == null);
      }
     
      public String getItemFromLocalStorage(String key) {
        return (String) js.executeScript(String.format(
            "return window.localStorage.getItem('%s');", key));
      }
     
      public String getKeyFromLocalStorage(int key) {
        return (String) js.executeScript(String.format(
            "return window.localStorage.key('%s');", key));
      }
     
      public Long getLocalStorageLength() {
        return (Long) js.executeScript("return window.localStorage.length;");
      }
     
      public void setItemInLocalStorage(String item, String value) {
        js.executeScript(String.format(
            "window.localStorage.setItem('%s','%s');", item, value));
      }
     
      public void clearLocalStorage() {
        js.executeScript(String.format("window.localStorage.clear();"));
      }
      
      public String fileDownloader(WebElement element, String path, String fileName) throws Exception {
          FileDownload = new FileDownloader();
          String downloadPath = ((System.getProperty("user.dir"))+ "\\src\\test\\resources\\com\\infomentum\\" + path + "\\");
          //String downloadPath = "C:\\dev\\";
          return FileDownload.fileDownloader(driver, element, downloadPath, fileName, 30);
      }
    
    // close the web browser
      
    public byte[] closeTheBrowser(boolean failed) throws Exception {
        if(driver!=null) {
            if (failed == true && screenshot==null) {
                screenshot = takeScreenshotFullScreenAsByte();
            }
            else if (failed == true && screenshot!=null) {
                
            }
            driver.quit();
        }
        else {
            System.out.println("screenshot not available");
            screenshot = "Screenshot not available".getBytes();
        }   
        
        return screenshot;
    }
    
    // for hiding a we element
    
    public void hideElement(Locators locator, String element) throws Exception {
        WebElement e = getWebElement(locator,element);
        ((JavascriptExecutor)driver).executeScript("arguments[0].style.visibility='hidden'", e);
    }
    
    // compare images
    
    public boolean compareImages (String exp, String cur, String diff) throws Exception {
        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();

        // For metric-output
        compare.setErrorConsumer(StandardStream.STDERR);
        IMOperation cmpOp = new IMOperation();
        // Set the compare metric
        cmpOp.metric("mae");
        //cmpOp.dissimilarityThreshold(100.0);
        //cmpOp.subimageSearch();
        //cmpOp.fuzz(95.0);

        // Add the expected image
        cmpOp.addImage(exp);

        // Add the current image
        cmpOp.addImage(cur);

        // This stores the difference
        cmpOp.addImage(diff);

        try {
            // Do the compare
            compare.run(cmpOp);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    // run a visual comparison
    
    public boolean visualComparisonPage(String name, Scenario scenario, String elementName) throws Exception {
        
        //Create screenshot parent folder if not exist
        createFolder(parentScreenShotsLocation); 
        
        //Create a specific directory for a test
        testScreenShotDirectory = parentScreenShotsLocation + name.replace(" ", "") + "\\";
        createFolder(testScreenShotDirectory);
        
        //Take ScreenShot with AShot
        Screenshot screenshot = ashotPage();
        
        //Declare element screenshot paths
        declareScreenShotPaths("Baseline.png", "Actual.png", "Diff.png");
        
        //Write actual image to the test screenshot path
        ImageIO.write(screenshot.getImage(), "PNG", actualImageFile);
        
        //Do image comparison
        return doComparison(screenshot, name, scenario, elementName);
    }
    
    
    // run a visual comparison
    
    public boolean visualComparisonElement(String name, Locators locator, String element, Scenario scenario, String testName) throws Exception {
        
        // locate the element
        WebElement elementCompare = getWebElement(locator,element);
        return visualComparisonElement(name,elementCompare,scenario, testName);
    }    
    
    // run a visual comparison
    
    public boolean visualComparisonElement(String name, WebElement element, Scenario scenario, String testName) throws Exception {
        //Create screenshot parent folder if not exist
        createFolder(parentScreenShotsLocation);
        //Create a specific directory for a test
        testScreenShotDirectory = parentScreenShotsLocation + name.replace(" ", "") + "\\";
        createFolder(testScreenShotDirectory);
        //scrollToElement(element);
        wait(3);
        
        //Take ScreenShot with AShot
        Screenshot screenshot = new AShot()
          //.addIgnoredElement(By.id(excludeLocate)) // ignored element(s)
          .takeScreenshot(driver, element);
        //Declare element screenshot paths
        declareScreenShotPaths(testName+"-Baseline.png", testName+"-Actual.png", testName+"-Diff.png");
        
        //Write actual image to the test screenshot path
        ImageIO.write(screenshot.getImage(), "PNG", actualImageFile);
        
        //Do image comparison
        return doComparison(screenshot, name, scenario, testName);
    }
    
    //Screenshot paths
    private void declareScreenShotPaths (String baseline, String actual, String diff) {
        //BaseLine, Current, Difference Photo Paths
        baselineScreenShotPath = testScreenShotDirectory + baseline;
        actualScreenShotPath = testScreenShotDirectory + actual;
        differenceScreenShotPath = testScreenShotDirectory + diff;
    
        //BaseLine, Current Photo Files
        baselineImageFile = new File(baselineScreenShotPath);
        actualImageFile = new File(actualScreenShotPath);
    }
    
    //ImageMagick Compare Method
    private boolean compareImagesWithImageMagick (String exp, String cur, String diff, Scenario scenario) throws Exception {


        ProcessStarter.setGlobalSearchPath("C:\\Program Files\\ImageMagick-7.0.8-Q16");
    
        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();
    
        // For metric-output
        compare.setErrorConsumer(StandardStream.STDERR);
        IMOperation cmpOp = new IMOperation();
    
        //Set the compare metric
        //cmpOp.metric("AE").fuzz(0.5);
        cmpOp.fuzz(0.5);
        cmpOp.metric("AE");
    
        // Add the expected image
        cmpOp.addImage(exp);
    
        // Add the current image
        cmpOp.addImage(cur);
    
        // This stores the difference
        cmpOp.addImage(diff);
    
        try {
            //Do the compare
            System.out.println ("Comparison Started!");
            compare.run(cmpOp);
            System.out.println ("Comparison Finished!");
                        
            Path path = Paths.get(exp);
            byte[] expectedBytes = Files.readAllBytes(path);
            scenario.embed(expectedBytes, "image/png");
            
            path = Paths.get(cur);
            byte[] actualBytes = Files.readAllBytes(path);
            scenario.embed(actualBytes, "image/png");
            
            //DisplayCmd.show(diff);
            return true;
        }
        catch (Exception ex) {
            System.out.print(ex);
            
            scenario.write("Comparison failed - images are not matching");
            
            Path path = Paths.get(exp);
            byte[] expectedBytes = Files.readAllBytes(path);
            scenario.embed(expectedBytes, "image/png");
            
            path = Paths.get(cur);
            byte[] actualBytes = Files.readAllBytes(path);
            scenario.embed(actualBytes, "image/png");
            
            path = Paths.get(diff);
            byte[] diffBytes = Files.readAllBytes(path);
            scenario.embed(diffBytes, "image/png");
                        
            return false;
            //throw ex;
        }
    }
    
    //Compare Operation
    private boolean doComparison (Screenshot elementScreenShot, String name, Scenario scenario, String testName) throws Exception {
        
        boolean compareResult = true;
        
        JiraHelper jira = new JiraHelper("Basic");
        
        BufferedImage image = null;
        
        // Get the baseline from jira
        try {
            URI attachmentURI = jira.getIssueAttachment(name, testName+"-Baseline.png");
            if (attachmentURI != null) {
                URL url = new URL((attachmentURI.toURL().toString()));

                try{

                    HttpClient hc = new HttpClient();
                    GetMethod get = new GetMethod(url.toString());
                    BASE64Encoder base64encoder = new BASE64Encoder();
                    byte[] b = "selenium:Weblogic@123".getBytes();
                    String cp = base64encoder.encode(b);
                    get.setRequestHeader("Authorization", "Basic "+cp);
                    int code = hc.executeMethod(get);
                    System.out.println("Code:"+code);
                    InputStream is = get.getResponseBodyAsStream();
                    if(is!=null){
                        image = ImageIO.read(is);
                    }else{
                        System.out.println("Download failed");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                //image = ImageIO.read(url);
                ImageIO.write(image, "png",(new File(baselineImageFile.toPath().toString())));;
                long end = System.currentTimeMillis() + (30 * 1000);
                
                System.out.println(baselineImageFile.exists());
                while ((baselineImageFile.exists()==false)&&(System.currentTimeMillis() < end)) {
                    wait(1);   
                }
                
            }
            
            else {
                System.out.println("removing basline");
                Path path = Paths.get(baselineScreenShotPath);
                path.toFile().delete();
            }
        }
        
        catch (Exception e) {
            throw e;
        }
        
        //Did we capture baseline image before?
        if (baselineImageFile.exists()){
            //Compare screenshot with baseline
            System.out.println("Comparison method will be called!\n");
    
            System.out.println("Baseline: " + baselineScreenShotPath + "\n" +
                    "Actual: " + actualScreenShotPath + "\n" +
                    "Diff: " + differenceScreenShotPath);
    
            //Try to use IM API for comparison
            compareResult = compareImagesWithImageMagick(baselineScreenShotPath, actualScreenShotPath, differenceScreenShotPath, scenario);
    
            //If comparison result true print that it passed.
            if(compareResult==true) {
                System.out.println ("Comparison Passed!");
                scenario.write("Comparison passed");
            }
        } 
        else {
            System.out.println("BaselineScreenshot does not exist! We put it into test screenshot folder.\n");
            //Put the screenshot to the specified folder
            ImageIO.write(elementScreenShot.getImage(), "PNG", baselineImageFile);
            scenario.write("Creating the baseline");
            // store in jira
            Path path = Paths.get(baselineScreenShotPath);
            byte[] actualBytes = Files.readAllBytes(path);
            jira.attachBaseline(name, actualBytes, testName);
        }
        
        return compareResult;
    }
    
    //Create Folder Method
    private void createFolder (String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
            if (testDirectory.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        } else {
            System.out.println("Directory already exists: " + path);
        }
    }    
    
    public void doCompareNew(Scenario scenario) throws Exception {
        Screenshot screen1 = ashotPage();
        Screenshot screen2 = ashotPage();

          //.addIgnoredElement(By.cssSelector("#weather .blinking_element")) // ignored element(s)
        ImageDiff diff = new ImageDiffer().makeDiff(screen1, screen2);
        BufferedImage diffImage = diff.getMarkedImage(); // comparison result with marked differences
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(diffImage, "PNG", baos );
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        
        scenario.embed(imageInByte, "image/png");
    }
    
    // a method to test the redirect chain
    
    public List<String> redirectTest(String startUrl, String sessionId) throws IOException, Exception {
        
        List<String> urlChain = new ArrayList<String>();
        urlChain = crawl(startUrl,urlChain, sessionId);

        return urlChain;
    }
    
    private List<String> crawl(String url, List<String> urlChain, String sessionId) throws IOException, Exception {

        try {
            
            Response response = Jsoup.connect(url).cookie("JSESSIONID", sessionId).followRedirects(false).execute();
                        
            System.out.println(response.statusCode() + " : " + url);
            urlChain.add((response.statusCode() + " : " + url));
        
            // if it is redirected
            if (response.hasHeader("location")) {
                String redirectUrl = response.header("location");
                System.out.println(redirectUrl);
                //if (redirectUrl.contains("http")== false) {
                //    redirectUrl = "http://uat1-consump.motability.co.uk" + redirectUrl;
                //}
                //urlChain.add((response.statusCode() + " : " + redirectUrl));
                crawl(redirectUrl, urlChain, sessionId);
            }
            else {
                Document doc = response.parse();
                System.out.println(doc.title());
                urlChain.add(doc.title());
            }
        }
        
        catch (Exception e) {
          System.out.println(e.getMessage());
            urlChain.add("404 : " + url);
            urlChain.add("Error Handler");
        }
        
        return urlChain;
    }
    
    
    // read browser console logs
    
    public List<String> analyzeLog(String logLevel, String message) {
        
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        List<String> consoleErrors = new ArrayList<String>();
        
        if (logLevel != "") {
            
            Level level = Level.parse(logLevel);
            
            for (LogEntry entry : logEntries) {
                if (entry.getLevel().equals(level)) {
                    //assert(entry.getMessage().contains("violates the following Content Security ")==false) : "hkjhkj";
                    if (message != "") {
                        if (entry.getMessage().contains(message)) {
                            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                            consoleErrors.add(entry.getLevel().toString() + " - " + entry.getMessage());
                        }
                    }    
                    else {
                        System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                        consoleErrors.add(entry.getLevel().toString() + " - " + entry.getMessage());
                    }
               }
            }
        }
        
        else {
            for (LogEntry entry : logEntries) {
                if (message != "") {
                    if (entry.getMessage().contains(message)) {
                        System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                        consoleErrors.add(entry.getLevel().toString() + " - " + entry.getMessage());
                    }
                }    
                else {
                    System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                    consoleErrors.add(entry.getLevel().toString() + " - " + entry.getMessage());
                }
            }
        }   
        return consoleErrors;
    }
    
//    public List<String> accessibilityScanner(String url, Scenario scenario) throws Exception {
//
//        // load the page to test
//        if (url != null) {
//            loadPageSameWindow(url);
//        }
//        AccessibilityScanner scanner = new AccessibilityScanner(driver);
//        Map<String, Object> audit_report = scanner.runAccessibilityAudit();
//
//        assert(audit_report!=null);
//        if (audit_report.containsKey("plain_report")) {
//            scenario.write(audit_report.get("plain_report").toString());
//        }
//
//        List<Result> errors = (List<Result>) audit_report.get("error");
//        List<String> errorStrings = new ArrayList<String>();
//
//        for (Result error : errors) {
//            errorStrings.add(error.getRule());
//            log.info(error.getRule());//e.g. AX_TEXT_01 (Controls and media ....
//            log.info(error.getUrl());//e.g. See https://github.com/GoogleChrome/accessibility-developer-tools/wiki....
//            for (String element : error.getElements())
//                log.info(element);//e.g. #myForm > P > INPUT
//            }
//
//            //                      One can add asserts like these
//            //                      assertThat("No accessibility errors expected", errors.size(),
//            //                                      equalTo(0));
//
//        try {
//            if (audit_report != null && audit_report.containsKey("screenshot")) {
//                final byte[] screenshot = (byte[]) audit_report.get("screenshot");
//                log.warn("Writing screenshot ");
//                scenario.embed(screenshot, "image/png");
//            }
//        }
//        finally {
//            //driver.quit();
//        }
//        return errorStrings;
//    }
}
