package com.frostwire.gui.library;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.frostwire.alexandria.Library;
import com.frostwire.alexandria.Playlist;
import com.frostwire.alexandria.PlaylistItem;
import com.frostwire.alexandria.db.LibraryDatabase;
import com.frostwire.gui.player.AudioPlayer;
import com.frostwire.gui.player.AudioSource;
import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.IconButton;
import com.limegroup.gnutella.gui.options.ConfigureOptionsAction;
import com.limegroup.gnutella.gui.options.OptionsConstructor;
import com.limegroup.gnutella.gui.util.DividerLocationSettingUpdater;
import com.limegroup.gnutella.settings.LibrarySettings;
import com.limegroup.gnutella.settings.UISettings;

public class LibraryMediator {

    public static final String FILES_TABLE_KEY = "LIBRARY_FILES_TABLE";
    public static final String PLAYLISTS_TABLE_KEY = "LIBRARY_PLAYLISTS_TABLE";

    private static JPanel MAIN_PANEL;

    private LibraryPlaylists LIBRARY_PLAYLISTS;

    /**
     * Singleton instance of this class.
     */
    private static LibraryMediator INSTANCE;

    private LibraryFiles libraryFiles;
    private LibraryLeftPanel libraryLeftPanel;
    private LibrarySearch librarySearch;
    private LibraryCoverArt libraryCoverArt;

    private static Library LIBRARY;

    private CardLayout _tablesViewLayout = new CardLayout();
    private JPanel _tablesPanel;
    private JSplitPane splitPane;
    
    private Map<Object,Integer> scrollbarValues;
	private Object lastSelectedKey;
	private AbstractLibraryTableMediator<?, ?, ?> lastSelectedMediator;

    /**
     * @return the <tt>LibraryMediator</tt> instance
     */
    public static LibraryMediator instance() {
        if (INSTANCE == null) {
            INSTANCE = new LibraryMediator();
        }
        return INSTANCE;
    }

    public LibraryMediator() {
        GUIMediator.setSplashScreenString(I18n.tr("Loading Library Window..."));

        getComponent(); // creates MAIN_PANEL
        
        scrollbarValues =  new HashMap<Object, Integer>();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getLibraryLeftPanel(), getLibraryRightPanel());
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JSplitPane splitPane = (JSplitPane) evt.getSource();
                int current = splitPane.getDividerLocation();
                if (current > LibraryLeftPanel.MAX_WIDTH) {
                    splitPane.setDividerLocation(LibraryLeftPanel.MAX_WIDTH);
                } else if (current < LibraryLeftPanel.MIN_WIDTH) {
                    splitPane.setDividerLocation(LibraryLeftPanel.MIN_WIDTH);
                }

            }
        });

        DividerLocationSettingUpdater.install(splitPane, UISettings.UI_LIBRARY_MAIN_DIVIDER_LOCATION);

        MAIN_PANEL.add(splitPane);
    }
    
    protected Object getSelectedKey() {
		if (getSelectedPlaylist() != null) {
			return getSelectedPlaylist();
		} else {
			return getLibraryFiles().getSelectedDirectoryHolder();
		}
	}

	public static Library getLibrary() {
        if (LIBRARY == null) {
            LIBRARY = new Library(LibrarySettings.LIBRARY_DATABASE);
        }
        return LIBRARY;
    }

    public LibraryFiles getLibraryFiles() {
        if (libraryFiles == null) {
            libraryFiles = new LibraryFiles();
        }
        return libraryFiles;
    }

    public LibraryPlaylists getLibraryPlaylists() {
        if (LIBRARY_PLAYLISTS == null) {
            LIBRARY_PLAYLISTS = new LibraryPlaylists();
        }
        return LIBRARY_PLAYLISTS;
    }

    /**
     * Returns null if none is selected.
     * @return
     */
    public Playlist getSelectedPlaylist() {
        return getLibraryPlaylists().getSelectedPlaylist();
    }

    public LibrarySearch getLibrarySearch() {
        if (librarySearch == null) {
            librarySearch = new LibrarySearch();
        }
        return librarySearch;
    }

    public LibraryCoverArt getLibraryCoverArt() {
        if (libraryCoverArt == null) {
            libraryCoverArt = new LibraryCoverArt();
        }
        return libraryCoverArt;
    }

    public JComponent getComponent() {
        if (MAIN_PANEL == null) {
            MAIN_PANEL = new JPanel(new BorderLayout());
        }
        return MAIN_PANEL;
    }

    public void showView(String key) {
        _tablesViewLayout.show(_tablesPanel, key);
        rememberScrollbarsOnMediators(key);        
    }
    
    private void rememberScrollbarsOnMediators(String key) {
        AbstractLibraryListPanel listPanel = null;
    	AbstractLibraryTableMediator<?, ?, ?> tableMediator = null;
    	
        if (key.equals(FILES_TABLE_KEY)) {
    		listPanel = getLibraryFiles();
    		tableMediator = LibraryFilesTableMediator.instance();
    		
    	} else if (key.equals(PLAYLISTS_TABLE_KEY)) {
    		listPanel = getLibraryPlaylists();
    		tableMediator = LibraryPlaylistsTableMediator.instance();
    	}
        
        if (lastSelectedMediator != null && lastSelectedKey != null) {
        	System.out.println(lastSelectedKey + " SCROLLBAR LAST VALUE " + lastSelectedMediator.getScrollbarValue());
        	scrollbarValues.put(lastSelectedKey, lastSelectedMediator.getScrollbarValue());
        }

        lastSelectedMediator = tableMediator;
        lastSelectedKey = getSelectedKey();
        
        
        if (scrollbarValues.containsKey(lastSelectedKey)) {
        	final int lastScrollValue = scrollbarValues.get(lastSelectedKey);
        	
        	if (tableMediator == null || listPanel == null) {
        		//nice antipattern here.
        		return;
        	}
        	
        	//oh java...
        	final AbstractLibraryListPanel finalListPanel = listPanel;
        	final AbstractLibraryTableMediator<?, ?, ?> finalTableMediator = tableMediator;
        	
        	finalListPanel.enqueueRunnable(new Runnable() {
    			public void run() {
    				 GUIMediator.safeInvokeLater(new Runnable() {

						@Override
						public void run() {
							System.out.println("SCROLL TO " + lastScrollValue);
							finalTableMediator.scrollTo(lastScrollValue);
						}    					 
    				 });
    				
    			}
    		});
        }
    }

    public void updateTableFiles(DirectoryHolder dirHolder) {
        LibraryFilesTableMediator.instance().updateTableFiles(dirHolder);
        showView(FILES_TABLE_KEY);
    }

    public void updateTableItems(Playlist playlist) {
        LibraryPlaylistsTableMediator.instance().updateTableItems(playlist);
        showView(PLAYLISTS_TABLE_KEY);
    }

    public void clearLibraryTable() {
        LibraryFilesTableMediator.instance().clearTable();
        LibraryPlaylistsTableMediator.instance().clearTable();
        getLibrarySearch().clear();
    }

    public void addFilesToLibraryTable(List<File> files) {
        for (File file : files) {
            LibraryFilesTableMediator.instance().add(file);
        }
        getLibrarySearch().addResults(files.size());
    }

    public void addItemsToLibraryTable(List<PlaylistItem> items) {
        for (PlaylistItem item : items) {
            LibraryPlaylistsTableMediator.instance().add(item);
        }
        getLibrarySearch().addResults(items.size());
    }

    private JComponent getLibraryLeftPanel() {
        if (libraryLeftPanel == null) {
            libraryLeftPanel = new LibraryLeftPanel(getLibraryFiles(), getLibraryPlaylists(), getLibraryCoverArt());
        }
        return libraryLeftPanel;
    }

    private JComponent getLibraryRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        _tablesViewLayout = new CardLayout();
        _tablesPanel = new JPanel(_tablesViewLayout);

        _tablesPanel.add(LibraryFilesTableMediator.instance().getScrolledTablePane(), FILES_TABLE_KEY);
        _tablesPanel.add(LibraryPlaylistsTableMediator.instance().getScrolledTablePane(), PLAYLISTS_TABLE_KEY);

        panel.add(getLibrarySearch(), BorderLayout.PAGE_START);
        panel.add(_tablesPanel, BorderLayout.CENTER);

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(
                new IconButton(new ConfigureOptionsAction(OptionsConstructor.SHARED_KEY, I18n.tr("Options"), I18n
                        .tr("You can configure the folders you share in FrostWire\'s Options."))), BorderLayout.LINE_START);
        panelBottom.add(new LibraryPlayer(), BorderLayout.CENTER);
        panel.add(panelBottom, BorderLayout.PAGE_END);

        return panel;
    }

    public void setSelectedFile(File file) {
        getLibraryFiles().selectFinishedDownloads();
        LibraryFilesTableMediator.instance().setFileSelected(file);
    }

    public void selectCurrentSong() {
        //Select current playlist.
        Playlist currentPlaylist = AudioPlayer.instance().getCurrentPlaylist();
        final AudioSource currentSong = AudioPlayer.instance().getCurrentSong();

        //If the current song is being played from a playlist.
        if (currentPlaylist != null && currentSong != null && currentSong.getPlaylistItem() != null) {
            if (currentPlaylist.getId() != LibraryDatabase.STARRED_PLAYLIST_ID) {

                //select the song once it's available on the right hand side
                getLibraryPlaylists().enqueueRunnable(new Runnable() {
                    public void run() {
                        GUIMediator.safeInvokeLater(new Runnable() {
                            public void run() {
                                LibraryPlaylistsTableMediator.instance().setPlaylistItemSelected(currentSong.getPlaylistItem());
                            }
                        });
                    }
                });

                //select the playlist
                getLibraryPlaylists().selectPlaylist(currentPlaylist);
            } else {
                LibraryFiles libraryFiles = getLibraryFiles();

                //select the song once it's available on the right hand side
                libraryFiles.enqueueRunnable(new Runnable() {
                    public void run() {
                        GUIMediator.safeInvokeLater(new Runnable() {
                            public void run() {
                                LibraryPlaylistsTableMediator.instance().setPlaylistItemSelected(currentSong.getPlaylistItem());
                            }
                        });
                    }
                });

                libraryFiles.selectStarred();
            }

        } else if (currentSong != null && currentSong.getFile() != null) {
            //selects the audio node at the top
            LibraryFiles libraryFiles = getLibraryFiles();

            //select the song once it's available on the right hand side
            libraryFiles.enqueueRunnable(new Runnable() {
                public void run() {
                    GUIMediator.safeInvokeLater(new Runnable() {
                        public void run() {
                            LibraryFilesTableMediator.instance().setFileSelected(currentSong.getFile());
                        }
                    });
                }
            });

            libraryFiles.selectAudio();
        }

        //Scroll to current song.
    }
}
