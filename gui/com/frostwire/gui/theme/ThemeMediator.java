/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, 2013, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.limewire.util.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.swing.SwingUtilities2;

import com.apple.laf.AquaFonts;
import com.limegroup.gnutella.settings.ApplicationSettings;

/**
 * Class that mediates between themes and FrostWire.
 * 
 * 
 * @author gubatron
 * @author aldenml
 *
 */
public final class ThemeMediator {

    private static final Logger LOG = LoggerFactory.getLogger(ThemeMediator.class);

    public static final Font DIALOG_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);

    public static final Color LIGHT_BORDER_COLOR = SkinColors.GENERAL_BORDER_COLOR;

    public static final Color TABLE_ALTERNATE_ROW_COLOR = SkinColors.TABLE_ALTERNATE_ROW_COLOR;
    public static final Color TABLE_SELECTED_BACKGROUND_ROW_COLOR = SkinColors.TABLE_SELECTED_BACKGROUND_ROW_COLOR;

    public static final Color TAB_BUTTON_FOREGROUND_COLOR = new Color(0xFFFFFF);

    public static final String SKIN_PROPERTY_DARK_BOX_BACKGROUND = "skin_property_dark_box_background";

    public static Color PLAYING_DATA_LINE_COLOR = new Color(7, 170, 0);

    public static Color FILE_NO_EXISTS_DATA_LINE_COLOR = Color.RED;

    private ThemeMediator() {
    }

    public static void changeTheme() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {

                    try {
                        UIManager.setLookAndFeel(new NimbusLookAndFeel() {
                            @Override
                            public UIDefaults getDefaults() {
                                return modifyNimbusDefaults(super.getDefaults());
                            }
                        });
                        applySkinSettings();

                    } catch (Exception e) {
                        LOG.error("Unable to change the L&F", e);
                    }
                }
            });
        } catch (Exception e) {
            LOG.error("Unable to change the L&F", e);
        }
    }

    public static Font fixLabelFont(JLabel label) {
        return fixComponentFont(label, label.getText());
    }

    public static Font fixComponentFont(JComponent c, Object msg) {
        Font oldFont = null;

        if (c != null && OSUtils.isWindows()) {
            Font currentFont = c.getFont();
            if (currentFont != null && !canDisplayMessage(currentFont, msg)) {
                oldFont = currentFont;
                c.setFont(ThemeMediator.DIALOG_FONT);
            }
        }

        return oldFont;
    }

    public static TitledBorder createTitledBorder(String title) {
        return new SkinTitledBorder(title);
    }

    static void testComponentCreationThreadingViolation() {
        if (!SwingUtilities.isEventDispatchThread()) {
            UiThreadingViolationException uiThreadingViolationError = new UiThreadingViolationException("Component creation must be done on Event Dispatch Thread");
            uiThreadingViolationError.printStackTrace(System.err);
            throw uiThreadingViolationError;
        }
    }

    private static void applyCommonSkinUI() {
        UIManager.put("PopupMenuUI", "com.frostwire.gui.theme.SkinPopupMenuUI");
        UIManager.put("MenuItemUI", "com.frostwire.gui.theme.SkinMenuItemUI");
        UIManager.put("MenuUI", "com.frostwire.gui.theme.SkinMenuUI");
        UIManager.put("CheckBoxMenuItemUI", "com.frostwire.gui.theme.SkinCheckBoxMenuItemUI");
        UIManager.put("MenuBarUI", "com.frostwire.gui.theme.SkinMenuBarUI");
        UIManager.put("RadioButtonMenuItemUI", "com.frostwire.gui.theme.SkinRadioButtonMenuItemUI");
        UIManager.put("PopupMenuSeparatorUI", "com.frostwire.gui.theme.SkinPopupMenuSeparatorUI");
        UIManager.put("FileChooserUI", "com.frostwire.gui.theme.SkinFileChooserUI");
        UIManager.put("TabbedPaneUI", "com.frostwire.gui.theme.SkinTabbedPaneUI");
        UIManager.put("OptionPaneUI", "com.frostwire.gui.theme.SkinOptionPaneUI");
        UIManager.put("LabelUI", "com.frostwire.gui.theme.SkinLabelUI");
        UIManager.put("ProgressBarUI", "com.frostwire.gui.theme.SkinProgressBarUI");
        UIManager.put("PanelUI", "com.frostwire.gui.theme.SkinPanelUI");
        UIManager.put("ScrollBarUI", "com.frostwire.gui.theme.SkinScrollBarUI");
        UIManager.put("ScrollPaneUI", "com.frostwire.gui.theme.SkinScrollPaneUI");
        UIManager.put("SplitPaneUI", "com.frostwire.gui.theme.SkinSplitPaneUI");
        UIManager.put("ApplicationHeaderUI", "com.frostwire.gui.theme.SkinApplicationHeaderUI");
        UIManager.put("MultilineToolTipUI", "com.frostwire.gui.theme.SkinMultilineToolTipUI");
        UIManager.put("TreeUI", "com.frostwire.gui.theme.SkinTreeUI");
        UIManager.put("TextFieldUI", "com.frostwire.gui.theme.SkinTextFieldUI");
        UIManager.put("RangeSliderUI", "com.frostwire.gui.theme.SkinRangeSliderUI");
        UIManager.put("TableUI", "com.frostwire.gui.theme.SkinTableUI");
        UIManager.put("RadioButtonUI", "com.frostwire.gui.theme.SkinRadioButtonUI");
    }

    private static FontUIResource getControlFont() {
        FontUIResource font = null;
        if (OSUtils.isWindows()) {
            Font recommendedFont = fixWindowsOSFont();
            if (recommendedFont != null) {
                font = new FontUIResource(recommendedFont);
            }
        } else if (OSUtils.isMacOSX()) {
            font = AquaFonts.getControlTextFont();
        } else if (OSUtils.isLinux()) {
            Font recommendedFont = fixLinuxOSFont();
            if (recommendedFont != null) {
                font = new FontUIResource(recommendedFont);
            }
        }

        return font;
    }

    private static String getRecommendedFontName() {
        String fontName = null;

        String language = ApplicationSettings.getLanguage();
        if (language != null) {
            if (language.startsWith("ja")) {
                //Meiryo for Japanese
                fontName = "Meiryo";
            } else if (language.startsWith("ko")) {
                //Malgun Gothic for Korean
                fontName = "Malgun Gothic";
            } else if (language.startsWith("zh")) {
                //Microsoft JhengHei for Chinese (Traditional)
                //Microsoft YaHei for Chinese (Simplified)
                fontName = "Microsoft JhengHei";
            } else if (language.startsWith("he")) {
                //Gisha for Hebrew
                fontName = "Gisha";
            } else if (language.startsWith("th")) {
                //Leelawadee for Thai
                fontName = "Leelawadee";
            }
        }
        return fontName;
    }

    private static boolean canDisplayMessage(Font f, Object msg) {
        boolean result = true;

        if (msg instanceof String) {
            String s = (String) msg;
            result = f.canDisplayUpTo(s) == -1;
        }

        return result;
    }

    private static void applySkinSettings() {
        applyCommonSkinUI();

        fixAAFontSettings();

        UIManager.put("Tree.leafIcon", UIManager.getIcon("Tree.closedIcon"));

        // remove split pane borders
        UIManager.put("SplitPane.border", BorderFactory.createEmptyBorder());

        if (!OSUtils.isMacOSX()) {
            UIManager.put("Table.focusRowHighlightBorder", UIManager.get("Table.focusCellHighlightBorder"));
        }

        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // Add a bold text version of simple text.
        Font normal = UIManager.getFont("Table.font");
        FontUIResource bold = new FontUIResource(normal.getName(), Font.BOLD, normal.getSize());
        UIManager.put("Table.font.bold", bold);
        UIManager.put("Tree.rowHeight", 0);
    }

    private static void fixAAFontSettings() {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        boolean lafCond = SwingUtilities2.isLocalDisplay();
        Object aaTextInfo = SwingUtilities2.AATextInfo.getAATextInfo(lafCond);
        defaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aaTextInfo);
    }

    // windows font policy http://msdn.microsoft.com/en-us/library/windows/desktop/aa511282.aspx
    // table of languages http://msdn.microsoft.com/en-us/library/ee825488(v=cs.20).aspx
    private static Font fixWindowsOSFont() {
        Font font = null;
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            Method method = Toolkit.class.getDeclaredMethod("setDesktopProperty", String.class, Object.class);
            method.setAccessible(true);

            String fontName = ThemeMediator.getRecommendedFontName();

            if (fontName != null) {
                font = new Font(fontName, Font.PLAIN, 12);
                method.invoke(toolkit, "win.icon.font", font);
                //SubstanceLookAndFeel.setFontPolicy(SubstanceFontUtilities.getDefaultFontPolicy());
            }
        } catch (Throwable e) {
            LOG.error("Error fixing font", e);
        }

        return font;
    }

    private static Font fixLinuxOSFont() {
        Font font = null;
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            Method method = Toolkit.class.getDeclaredMethod("setDesktopProperty", String.class, Object.class);
            method.setAccessible(true);

            String fontName = ThemeMediator.getRecommendedFontName();

            if (fontName != null) {
                // linux is hardcoded to Dialog
                fontName = "Dialog";
                font = new Font(fontName, Font.PLAIN, 12);
                method.invoke(toolkit, "gnome.Gtk/FontName", fontName);
                //SubstanceLookAndFeel.setFontPolicy(SubstanceFontUtilities.getDefaultFontPolicy());
            }
        } catch (Throwable e) {
            LOG.error("Error fixing font", e);
        }

        return font;
    }

    private static UIDefaults modifyNimbusDefaults(UIDefaults defaults) {
        defaults.put("control", SkinColors.LIGHT_BACKGROUND_COLOR);
        //defaults.put("nimbusBase", new Color(SkinColors.GENERAL_BORDER_COLOR.getRGB()));
        defaults.put("nimbusSelection", SkinColors.TABLE_SELECTED_BACKGROUND_ROW_COLOR);

        // font color
        defaults.put("text", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        defaults.put("controlText", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        defaults.put("infoText", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        defaults.put("menuText", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        defaults.put("textForeground", SkinColors.TEXT_FONT_FOREGROUND_COLOR);

        FontUIResource font = getControlFont();
        if (font != null) {
            defaults.put("defaultFont", font);
        }

        defaults.put("Panel.background", SkinColors.LIGHT_BACKGROUND_COLOR);

        // progressbar
        int paddingEnabled = defaults.getInt("ProgressBar[Enabled+Indeterminate].progressPadding");
        int paddingDisabled = defaults.getInt("ProgressBar[Disabled+Indeterminate].progressPadding");

        defaults.put("ProgressBar[Enabled].foregroundPainter", new SkinProgressBarPainter(SkinProgressBarPainter.State.Enabled, paddingEnabled));
        defaults.put("ProgressBar[Enabled+Finished].foregroundPainter", new SkinProgressBarPainter(SkinProgressBarPainter.State.Enabled, paddingEnabled));
        defaults.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", new SkinProgressBarPainter(SkinProgressBarPainter.State.EnabledIndeterminate, paddingEnabled));
        defaults.put("ProgressBar[Disabled].foregroundPainter", new SkinProgressBarPainter(SkinProgressBarPainter.State.Disabled, paddingDisabled));
        defaults.put("ProgressBar[Disabled+Finished].foregroundPainter", new SkinProgressBarPainter(SkinProgressBarPainter.State.Disabled, paddingDisabled));
        defaults.put("ProgressBar[Disabled+Indeterminate].foregroundPainter", new SkinProgressBarPainter(SkinProgressBarPainter.State.DisabledIndeterminate, paddingDisabled));

        // scrollbar
        defaults.put("ScrollBar:\"ScrollBar.button\".size", Integer.valueOf(18));

        defaults.put("ScrollBar:\"ScrollBar.button\"[Disabled].foregroundPainter", new SkinScrollBarButtonPainter(SkinScrollBarButtonPainter.State.Disabled));
        defaults.put("ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter", new SkinScrollBarButtonPainter(SkinScrollBarButtonPainter.State.Enabled));
        defaults.put("ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter", new SkinScrollBarButtonPainter(SkinScrollBarButtonPainter.State.MouseOver));
        defaults.put("ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter", new SkinScrollBarButtonPainter(SkinScrollBarButtonPainter.State.Pressed));

        defaults.put("ScrollBar:ScrollBarTrack[Disabled].backgroundPainter", new SkinScrollBarTrackPainter(SkinScrollBarTrackPainter.State.Disabled));
        defaults.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", new SkinScrollBarTrackPainter(SkinScrollBarTrackPainter.State.Enabled));

        defaults.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", new SkinScrollBarThumbPainter(SkinScrollBarThumbPainter.State.Enabled));
        defaults.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", new SkinScrollBarThumbPainter(SkinScrollBarThumbPainter.State.MouseOver));
        defaults.put("ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", new SkinScrollBarThumbPainter(SkinScrollBarThumbPainter.State.Pressed));

        // tableheader
        defaults.put("TableHeader.background", SkinColors.LIGHT_BACKGROUND_COLOR);

        defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", new SkinTableHeaderPainter(SkinTableHeaderPainter.State.Enabled));
        defaults.put("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", new SkinTableHeaderPainter(SkinTableHeaderPainter.State.MouseOver));
        defaults.put("TableHeader:\"TableHeader.renderer\"[Pressed].backgroundPainter", new SkinTableHeaderPainter(SkinTableHeaderPainter.State.Pressed));

        // table
        defaults.put("Table.cellNoFocusBorder", new InsetsUIResource(0, 0, 0, 0));
        defaults.put("Table.focusCellHighlightBorder", new InsetsUIResource(0, 0, 0, 0));
        defaults.put("Table.alternateRowColor", new Color(SkinColors.TABLE_ALTERNATE_ROW_COLOR.getRGB()));
        defaults.put("Table[Enabled+Selected].textBackground", new Color(SkinColors.TABLE_SELECTED_BACKGROUND_ROW_COLOR.getRGB()));
        defaults.put("Table[Enabled+Selected].textForeground", SkinColors.TABLE_SELECTED_FOREGROUND_ROW_COLOR);

        // splitter
        defaults.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new SkinSplitPaneDividerBackgroundPainter(SkinSplitPaneDividerBackgroundPainter.State.Enabled));

        // tabbedpanetab
        defaults.put("TabbedPane:TabbedPaneTabArea.contentMargins", new InsetsUIResource(3, 4, 0, 4));
        defaults.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new SkinTabbedPaneTabAreaBackgroundPainter(SkinTabbedPaneTabAreaBackgroundPainter.State.Disabled));
        defaults.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new SkinTabbedPaneTabAreaBackgroundPainter(SkinTabbedPaneTabAreaBackgroundPainter.State.EnableMouseOver));
        defaults.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new SkinTabbedPaneTabAreaBackgroundPainter(SkinTabbedPaneTabAreaBackgroundPainter.State.EnablePressed));
        defaults.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new SkinTabbedPaneTabAreaBackgroundPainter(SkinTabbedPaneTabAreaBackgroundPainter.State.Enable));

        defaults.put("TabbedPane:TabbedPaneTab.contentMargins", new InsetsUIResource(3, 4, 4, 8));
        defaults.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.DisabledSelected));
        defaults.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.Disabled));
        defaults.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.EnabledMouseOver));
        defaults.put("TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.EnabledPressed));
        defaults.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.Enabled));
        defaults.put("TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.FocusedMouseOverSelected));
        defaults.put("TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.FocusedPressedSelected));
        defaults.put("TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.FocusedSelected));
        defaults.put("TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.MouseOverSelected));
        defaults.put("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.PressedSelected));
        defaults.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new SkinTabbedPaneTabBackgroundPainter(SkinTabbedPaneTabBackgroundPainter.State.Selected));

        // tree
        defaults.put("Tree.closedIcon", null);
        defaults.put("Tree.openIcon", null);
        defaults.put("Tree.leafIcon", null);
        defaults.put("Tree.selectionForeground", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        defaults.put("Tree:TreeCell[Enabled+Selected].textForeground", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        defaults.put("Tree:TreeCell[Focused+Selected].textForeground", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        //defaults.put("Tree.rendererFillBackground", Boolean.TRUE);

        // list
        defaults.put("List.cellNoFocusBorder", new InsetsUIResource(0, 0, 0, 0));
        defaults.put("List.focusCellHighlightBorder", new InsetsUIResource(0, 0, 0, 0));
        defaults.put("List[Selected].textBackground", new Color(SkinColors.TABLE_SELECTED_BACKGROUND_ROW_COLOR.getRGB()));
        defaults.put("List[Selected].textForeground", new Color(SkinColors.TEXT_FONT_FOREGROUND_COLOR.getRGB()));

        // popup
        defaults.put("PopupMenu[Disabled].backgroundPainter", new SkinPopupMenuBackgroundPainter(SkinPopupMenuBackgroundPainter.State.Disabled));
        defaults.put("PopupMenu[Enabled].backgroundPainter", new SkinPopupMenuBackgroundPainter(SkinPopupMenuBackgroundPainter.State.Enabled));

        // menuitem
        defaults.put("MenuItem[Enabled].textForeground", SkinColors.TEXT_FONT_FOREGROUND_COLOR);
        defaults.put("MenuItem[MouseOver].backgroundPainter", new SkinMenuItemBackgroundPainter(SkinMenuItemBackgroundPainter.State.MouseOver));

        // textfield
        //defaults.put("TextField.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        defaults.put("TextField[Disabled].borderPainter", new SkinTextFieldBorderPainter(SkinTextFieldBorderPainter.State.Disabled));
        defaults.put("TextField[Enabled].borderPainter", new SkinTextFieldBorderPainter(SkinTextFieldBorderPainter.State.Enabled));
        defaults.put("TextField[Focused].borderPainter", new SkinTextFieldBorderPainter(SkinTextFieldBorderPainter.State.Focused));
        defaults.put("TextField[Disabled].backgroundPainter", new SkinTextFieldBackgroundPainter(SkinTextFieldBackgroundPainter.State.Disabled));
        defaults.put("TextField[Enabled].backgroundPainter", new SkinTextFieldBackgroundPainter(SkinTextFieldBackgroundPainter.State.Enabled));
        defaults.put("TextField[Focused].backgroundPainter", new SkinTextFieldBackgroundPainter(SkinTextFieldBackgroundPainter.State.Focused));

        // scrollpane
        defaults.put("ScrollPane.background", new ColorUIResource(Color.WHITE));

        // editorpane
        defaults.put("EditorPane[Enabled].backgroundPainter", SkinColors.LIGHT_BACKGROUND_COLOR);

        // radio buttons
        //defaults.put("RadioButton.icon", new IconUIResource()); 
        defaults.put("RadioButton[Disabled+Selected].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.DisabledSelected));
        defaults.put("RadioButton[Disabled].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.Disabled));
        defaults.put("RadioButton[Enabled].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.Enabled));
        defaults.put("RadioButton[Focused+MouseOver+Selected].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.FocusedMouseOverSelected));
        defaults.put("RadioButton[Focused+MouseOver].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.FocusedMouseOver));
        defaults.put("RadioButton[Focused+Pressed+Selected].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.FocusedPressedSelected));
        defaults.put("RadioButton[Focused+Pressed].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.FocusedPressed));
        defaults.put("RadioButton[Focused+Selected].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.FocusedSelected));
        defaults.put("RadioButton[Focused].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.Focused));
        defaults.put("RadioButton[MouseOver+Selected].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.MouseOverSelected));
        defaults.put("RadioButton[MouseOver].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.MouseOver));
        defaults.put("RadioButton[Pressed+Selected].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.PressedSelected));
        defaults.put("RadioButton[Pressed].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.Pressed));
        defaults.put("RadioButton[Selected].iconPainter", new SkinRadioButtonIconPainter(SkinRadioButtonIconPainter.State.Selected));

        // checkbox
        defaults.put("CheckBox[Disabled+Selected].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.DisabledSelected));
        defaults.put("CheckBox[Disabled].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.Disabled));
        defaults.put("CheckBox[Enabled].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.Enabled));
        defaults.put("CheckBox[Focused+MouseOver+Selected].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.FocusedMouseOverSelected));
        defaults.put("CheckBox[Focused+MouseOver].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.FocusedMouseOver));
        defaults.put("CheckBox[Focused+Pressed+Selected].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.FocusedPressedSelected));
        defaults.put("CheckBox[Focused+Pressed].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.FocusedPressed));
        defaults.put("CheckBox[Focused+Selected].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.FocusedSelected));
        defaults.put("CheckBox[Focused].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.Focused));
        defaults.put("CheckBox[MouseOver+Selected].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.MouseOverSelected));
        defaults.put("CheckBox[MouseOver].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.MouseOver));
        defaults.put("CheckBox[Pressed+Selected].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.PressedSelected));
        defaults.put("CheckBox[Pressed].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.Pressed));
        defaults.put("CheckBox[Selected].iconPainter", new SkinCheckBoxIconPainter(SkinCheckBoxIconPainter.State.Selected));

        // slider
        defaults.put("Slider:SliderThumb[Disabled].backgroundPainter", new SkinSliderThumbPainter(SkinSliderThumbPainter.State.Disabled));
        defaults.put("Slider:SliderThumb[Enabled].backgroundPainter", new SkinSliderThumbPainter(SkinSliderThumbPainter.State.Enabled));
        defaults.put("Slider:SliderThumb[Focused+MouseOver].backgroundPainter", new SkinSliderThumbPainter(SkinSliderThumbPainter.State.FocusedMouseOver));
        defaults.put("Slider:SliderThumb[Focused+Pressed].backgroundPainter", new SkinSliderThumbPainter(SkinSliderThumbPainter.State.FocusedPressed));
        defaults.put("Slider:SliderThumb[Focused].backgroundPainter", new SkinSliderThumbPainter(SkinSliderThumbPainter.State.Focused));
        defaults.put("Slider:SliderThumb[MouseOver].backgroundPainter", new SkinSliderThumbPainter(SkinSliderThumbPainter.State.MouseOver));
        defaults.put("Slider:SliderThumb[Pressed].backgroundPainter", new SkinSliderThumbPainter(SkinSliderThumbPainter.State.Pressed));

        return defaults;
    }
}