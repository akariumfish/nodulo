package data;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import app.Applet;
import app.nRun;

import gui.nAlign;
import gui.nGUI;
import gui.nInterfCommand;
import gui.nInterfModel;
import gui.nInterface;
import gui.nModelBook;
import gui.nModelGroup;
import gui.nWidget;
import gui.nWidgetGroup;

public class sDataBook {
	
//	nWidgetGroup widgGroup_data_explorer = null;
//	
//	public void open_explorer(sValueBloc b) {
//		
////		nWidgetGroup sec = app.toolbox.addSection(" INTERFACE ");
//		
//		if (widgGroup_data_explorer == null) {
//			widgGroup_data_explorer = app.screen_gui.addWidgetGroup("data_explorer_win");
//			widgGroup_data_explorer.metode("explore_bloc", b);
//			widgGroup_data_explorer.getGroup("window").get("close")
//			.addEventTrigger(new nRunnable() { public void run() {
//				widgGroup_data_explorer = null; }});
//		}
//	}
//	
//	public void build_section() {
//		
//		nWidgetGroup sec = app.toolbox.addSection(" EXPLORER ");
//		
//		nWidgetGroup data_explorer = app.screen_gui.addWidgetGroup("data_explorer");
//		data_explorer.get("ref").setParent(sec.get("back"));
//		data_explorer.metode("explore_bloc", app.data);
//		
//	}
	
	public static sBloc_Builder dataview_builder = null, blocmenu_builder = null, 
			blocinterf_builder = null;
	
	public static void build(Applet app) {
//		build_blocinterf(app);
//		build_blocmenu(app);
//		build_dataview(app);
		build_explorer(app);
		
		build_fileexplo(app);
	}
	
	
	
	

	//     -----------   FILE EXPLORER

	public static sBloc_Builder file_builder;
	
	public static void build_fileexplo(Applet app) {
		
		nModelBook book = app.gui.book;
		float RS = book.RS;
		
		file_builder = new sBloc_Builder(app.data, "file_explorer")
			.setSolo(true)
			.setInitRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o;
				nWidgetGroup win = app.gui.addWidgetGroup("file_explorer");
				b.addObject("win", win);
				win.metode("link_to_bloc", b);
				win.addEventClear(new nRun() { public void run() {
					b.clear(); }});
				win.metode("run_tofront");
			}})
			.setClearRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o;
				nWidgetGroup w = b.object("win", nWidgetGroup.class);
				if (w != null) w.clear();
			}});
		
		app.data.addRootBlocBuilder(file_builder);
		

		book.newModelGroup("file_explorer", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup("complex_window");
				
				g.metode("set_title", "file_explorer");

				sFile file = new sFile(app.data);
				
				nInterface interf = app.gui.addInterface()
						.pop(g);
				
				interf.add_row();
				nWidget all_f_w = interf.add_row_switch(1, "A");
				interf.add_row_label(6," Select File : ");
				interf.add_row_trigg(3,"REFRESH", new nRun() { public void run() {
					g.metode("list_files"); }});
				interf.add_row();
				nWidgetGroup file_list = interf.add_picklist(8,4);
				
				interf.add_row();
				interf.add_row_label(6,"");
				nWidget load_w = interf.add_row_trigg(4,"LOAD");
				load_w.addEventTrigger(new nRun() { public void run() {
					g.metode("load_file"); }});

				interf.add_col_separator();

				interf.add_row();
				nWidget file_name_w = interf.add_row_label(10," Loaded Save_Bloc : ");
				interf.add_row();
				nWidgetGroup data_list = interf.add_treelist(8,6);

				interf.add_col_separator();

				interf.add_row();
				interf.add_row_label(4,"show:");
				nWidget view_data_w = interf.add_row_switch(3, "data");
				nWidget view_all_w = interf.add_row_switch(3, "all");

				interf.add_row();
				interf.add_row_label(2,"Edit: ");
				interf.add_row_field(8,"");
				interf.add_row();
				interf.add_row_trigg(3,"Add", new nRun() { public void run() {
					
				}});
				interf.add_row_trigg(3,"Change", new nRun() { public void run() {
					
				}});
				interf.add_row_trigg(3,"Delete", new nRun() { public void run() {
					
				}});
				
				interf.add_row();
				interf.add_row_trigg(4,"Save to File", new nRun() { public void run() {
					// save save_bloc to select file
				}});
				interf.add_row_label(2,"");
				nWidget build_w = interf.add_row_trigg(4,"Build Bloc");
				build_w.addEventTrigger(new nRun() { public void run() {
					g.metode("build_file"); }});
				
				interf.add_col_separator();

				
				
				interf.add_col();
				

				sValueBloc build_bloc = app.data.obtainBloc("file_expl_temp");
				
				interf.add_row();
				interf.add_row_label(10,"Bloc Builded From Save_Bloc :");
				interf.add_row();
				interf.add_row_label(6,"");
				interf.add_row_trigg(4,"Make Save_Bloc", new nRun() { public void run() {
					
				}});
				interf.add_row();
				nWidgetGroup bloc_list = interf.add_treelist(8,5);
				
				interf.add_col_separator();
				
				interf.add_row();
				interf.add_row_label(10,"To selected bloc/val :");
				interf.add_row();
				interf.add_row_label(1,"");
				interf.add_row_trigg(2,"Copy", new nRun() { public void run() {
					
				}});
				interf.add_row_trigg(2,"Paste", new nRun() { public void run() {
					
				}});
				interf.add_row_trigg(2,"Edit", new nRun() { public void run() {
					
				}});
				interf.add_row_trigg(2,"Delete", new nRun() { public void run() {
					
				}});
				interf.add_row_label(1,"");
				
				interf.add_row();
				interf.add_row_label(10,"To Explored Bloc");
				interf.add_row();
				interf.add_row_label(1,"");
				interf.add_row_trigg(4,"Copy To", new nRun() { public void run() {
					
				}});
				interf.add_row_trigg(4,"Copy From", new nRun() { public void run() {
					
				}});
				interf.add_row_label(1,"");
				
				
				interf.add_col_separator();
				interf.add_col_separator();
				
				
				interf.add_row();
				interf.add_row_label(10,"Explorer :");
				interf.add_row();

				nWidget exp_w = interf.add_row_label(10,"");
				exp_w.setBoundChild(true);
				
				nWidgetGroup exp = app.gui.addWidgetGroup("data_explorer");
				exp.get("ref").setParent(exp_w);
				exp.metode("explore_bloc", app.data);
				
				

				g.addMetode("list_files", new nRun() { public void run() {
					interf.change_current_list(file_list);
					FileHandle[] files = Gdx.files.local("/").list();
					for(FileHandle fl : files) 
						if (fl.extension().equals(app.data.file_ext_txt) || all_f_w.isOn()) 
							interf.add_list_entry(fl.name());
					if (!all_f_w.isOn()) for(FileHandle fl : files) 
						if (fl.extension().equals(app.data.data_ext_txt)) 
							interf.add_list_entry(fl.name());
				}});
				g.metode("list_files");
				

				g.addMetode("build_file", new nRun() { public void run() {
					interf.change_current_list(bloc_list);
					build_bloc.empty();
					if (file.isOpen) {
						app.data.no_build();
						build_bloc.load_from_bloc(file.getBloc());
						app.data.do_build();
						g.metode("build_buildbloc_entrys", build_bloc, "");
					}
				}});
				

				g.addMetode("build_buildbloc_entrys", new nRun() { public void run(Object o1, Object o2) {
					sValueBloc sb = (sValueBloc)o1;
					String incr = (String)o2;

					nWidget w = interf.add_list_entry(incr+sb.ref);
					
					for (sValue b : sb.values.all()) {
						interf.add_list_entry(incr+" |   "+b.ref);
						interf.go_up_tree();
					}
					
					for (sValueBloc b : sb.blocs.all()) g.metode("build_buildbloc_entrys", b, incr+" |   ");
					interf.go_up_tree();
				}});
				
				
				
				
				
				g.addMetode("load_file", new nRun() { public void run() {
					String file_name = (String)file_list.metodeGet("get_pick");
					FileHandle fl = Gdx.files.local(file_name);
					if (fl != null) {
						file.close();
						file.open(file_name);
						if (file.isOpen) {
							file.load();
							file_name_w.setText(" Viewing : "+file_name);
							interf.change_current_list(data_list);
							File_Bloc sbloc = file.getBloc();
							g.metode("build_savebloc_entrys", sbloc, "");
						}
					} else {
						file_name_w.setText(" Can't open : "+file_name);
						interf.change_current_list(data_list);
					}
				}});
				

				g.addMetode("build_savebloc_entrys", new nRun() { public void run(Object o1, Object o2) {
					File_Bloc sb = (File_Bloc)o1;
					String incr = (String)o2;

					nWidget w = interf.add_list_entry(incr+sb.ref+"   : "+sb.get_data_cnt());
					if (view_data_w.isOn()) {
						for (File_Data b : sb.getDataList()) {
								if (view_all_w.isOn() || !(b.ref.charAt(0) == '_')) {
								String txt = incr+"-"+b.ref;
								nWidget wd = interf.add_list_entry(txt);
								interf.go_up_tree();
							}
						}
					}
					for (File_Bloc b : sb.getBlocList()) g.metode("build_savebloc_entrys", b, incr+" | ");
					interf.go_up_tree();
					if (sb.ref.equals("")) w.setOn();
				}});
				

				
				
				
				g.addMetode("link_to_bloc", new nRun() { public void run(Object o) {
					sValueBloc b = (sValueBloc)o;
					g.metode("link_window_to_bloc", b);
				}});
				
				return g;
			} 
		} );
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
//	//-------------------------------------//
//	//-       BLOC INTERFACE               //
//	//-------------------------------------//
//	 
//	public static void build_interf_editor(Applet app, nWidgetGroup g, nWidget cont, 
//			nInterface main_interf, sValueBloc context_bloc) {
//		nModelBook book = app.gui.book;
//		float RS = book.RS;
//		nGUI gui = app.gui;
//		
//		nInterface build_interf = app.gui.addInterface()
//				.pop(cont);
//
//		nInterface command_interf = app.gui.addInterface()
//				.pop(cont);
//		
//		//        BUILD MODIF
//
//		build_interf.add_collapse_col("context");
//		
//		build_interf.add_row();
//		build_interf.add_row_label(10, "context"); 
//		
//		//      TO DO
//		
//		
//		
//		
//		build_interf.add_line();
//		build_interf.add_collapse_col("modify");
//		
//		build_interf.add_row();
//		nWidgetGroup dropmenu_file = gui.addWidgetGroup("dropmenu");
//		nWidget codesel_drop = build_interf.add_row_trigg(5, "COMANDE");
//
//		build_interf.add_row();
//		nWidget arg1_label = build_interf.add_row_label(1, "");
//		nWidget arg1_field = build_interf.add_row_field(3, "");
//		arg1_field.hide(); arg1_label.hide();
//		nWidget arg1_valsel_drop = build_interf.add_row_trigg(1, "VAL");
//		arg1_valsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, sValue> mev : cont_bloc.values.entrySet()) {
//						String sc = mev.getValue().ref;
//						nWidget w1 = (nWidget)dropmenu_file
//								.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//						w1.addEventTrigger(new nRun() { public void run() {
//							arg1_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg1_valsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg1_metsel_drop = build_interf.add_row_trigg(1, "MET");
//		arg1_metsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, nRun> mev : cont_bloc.metodes.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg1_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg1_metsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg1_bldsel_drop = build_interf.add_row_trigg(1, "BLD");
//		arg1_bldsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (sBloc_Builder mev : cont_bloc.bloc_builders) {
//					String sc = mev.ref;
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg1_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg1_bldsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg1_paramsel_drop = build_interf.add_row_trigg(1, "PRM");
//		arg1_paramsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				for (Map.Entry<String, String> mev : 
//					app.gui.params_def.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg1_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg1_paramsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg1_w_slide = build_interf.add_row_label(4, "W:10");
//		arg1_w_slide.addEventSliderChange(new nRun() { public void run() {
//			arg1_w_slide.setText("W:" + 
//					arg1_w_slide.getSliderValInRangeInteger(1, 10));
//			arg1_field.setText(""+arg1_w_slide.getSliderValInRangeInteger(1, 10));
//			}}).setSlider().hide();
//		
//		
//		nWidget arg2_label = build_interf.add_row_label(1, "");
//		nWidget arg2_field = build_interf.add_row_field(3, "");
//		arg2_field.hide(); arg2_label.hide();
//		nWidget arg2_valsel_drop = build_interf.add_row_trigg(1, "VAL");
//		arg2_valsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, sValue> mev : cont_bloc.values.entrySet()) {
//					String sc = mev.getValue().ref;
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg2_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg2_valsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg2_metsel_drop = build_interf.add_row_trigg(1, "MET");
//		arg2_metsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, nRun> mev : cont_bloc.metodes.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg2_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg2_metsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg2_bldsel_drop = build_interf.add_row_trigg(1, "BLD");
//		arg2_bldsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (sBloc_Builder mev : cont_bloc.bloc_builders) {
//					String sc = mev.ref;
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg2_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg2_bldsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg2_paramsel_drop = build_interf.add_row_trigg(1, "PRM");
//		arg2_paramsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				for (Map.Entry<String, String> mev : 
//					app.gui.params_def.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg2_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg2_paramsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg2_w_slide = build_interf.add_row_label(4, "W:10");
//		arg2_w_slide.addEventSliderChange(new nRun() { public void run() {
//			arg2_w_slide.setText("W:" + 
//					arg2_w_slide.getSliderValInRangeInteger(1, 10));
//			arg2_field.setText(""+arg2_w_slide.getSliderValInRangeInteger(1, 10));
//			}}).setSlider().hide();
//		
//
//		build_interf.add_row();
//		nWidget arg3_label = build_interf.add_row_label(1, "");
//		nWidget arg3_field = build_interf.add_row_field(3, "");
//		arg3_field.hide(); arg3_label.hide();
//		nWidget arg3_valsel_drop = build_interf.add_row_trigg(1, "VAL");
//		arg3_valsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, sValue> mev : cont_bloc.values.entrySet()) {
//					String sc = mev.getValue().ref;
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg3_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg3_valsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg3_metsel_drop = build_interf.add_row_trigg(1, "MET");
//		arg3_metsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, nRun> mev : cont_bloc.metodes.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg3_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg3_metsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg3_bldsel_drop = build_interf.add_row_trigg(1, "BLD");
//		arg3_bldsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (sBloc_Builder mev : cont_bloc.bloc_builders) {
//					String sc = mev.ref;
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg3_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg3_bldsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg3_paramsel_drop = build_interf.add_row_trigg(1, "PRM");
//		arg3_paramsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				for (Map.Entry<String, String> mev : 
//					app.gui.params_def.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg3_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg3_paramsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg3_w_slide = build_interf.add_row_label(4, "W:10");
//		arg3_w_slide.addEventSliderChange(new nRun() { public void run() {
//			arg3_w_slide.setText("W:" + 
//					arg3_w_slide.getSliderValInRangeInteger(1, 10));
//			arg3_field.setText(""+arg3_w_slide.getSliderValInRangeInteger(1, 10));
//			}}).setSlider().hide();
//		
//		
//		nWidget arg4_label = build_interf.add_row_label(1, "");
//		nWidget arg4_field = build_interf.add_row_field(3, "");
//		arg4_field.hide(); arg4_label.hide();
//		nWidget arg4_valsel_drop = build_interf.add_row_trigg(1, "VAL");
//		arg4_valsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, sValue> mev : cont_bloc.values.entrySet()) {
//					String sc = mev.getValue().ref;
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg4_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg4_valsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg4_metsel_drop = build_interf.add_row_trigg(1, "MET");
//		arg4_metsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (Entry<String, nRun> mev : cont_bloc.metodes.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg4_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg4_metsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg4_bldsel_drop = build_interf.add_row_trigg(1, "BLD");
//		arg4_bldsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				sValueBloc cont_bloc = main_interf.context_bloc;
//				if (cont_bloc != null)
//					for (sBloc_Builder mev : cont_bloc.bloc_builders) {
//					String sc = mev.ref;
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg4_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg4_bldsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg4_paramsel_drop = build_interf.add_row_trigg(1, "PRM");
//		arg4_paramsel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				for (Map.Entry<String, String> mev : 
//					app.gui.params_def.entrySet()) {
//					String sc = mev.getKey();
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun() { public void run() {
//						arg4_field.setText(sc); }}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", arg4_paramsel_drop); 
////				}});
//			}});
//		}}).hide();
//		nWidget arg4_w_slide = build_interf.add_row_label(4, "W:10");
//		arg4_w_slide.addEventSliderChange(new nRun() { public void run() {
//			arg4_w_slide.setText("W:" + 
//					arg4_w_slide.getSliderValInRangeInteger(1, 10));
//			arg4_field.setText(""+arg4_w_slide.getSliderValInRangeInteger(1, 10));
//			}}).setSlider().hide();
//		
//		
//		build_interf.add_row();
//		nWidget add_com_trig = build_interf.add_row_trigg(5, "ADD", new nRun() { public void run() {
//			String arg1 = arg1_field.getText();
//			String arg2 = arg2_field.getText();
//			String arg3 = arg3_field.getText();
//			String arg4 = arg4_field.getText();
//			nInterface.Code code = nInterface.strToCode(codesel_drop.getText());
//			if (code != null) {
//				if (arg1.length() > 0 && arg2.length() > 0 && 
//						arg3.length() > 0 && arg4.length() > 0) {
//					main_interf.newCommand(code, arg1, arg2, arg3, arg4);
//				} else if (arg1.length() > 0 && arg2.length() > 0 && arg3.length() > 0) {
//					main_interf.newCommand(code, arg1, arg2, arg3);
//				} else if (arg1.length() > 0 && arg2.length() > 0) {
//					main_interf.newCommand(code, arg1, arg2);
//				} else if (arg1.length() > 0) {
//					main_interf.newCommand(code, arg1);
//				} else { main_interf.newCommand(code); }
//				if (main_interf.command_addition_index == 0) 
//					main_interf.rebuild_from_command_list();
//			}
//		}});
//		add_com_trig.hide();
//
//		codesel_drop.addEventTrigger(new nRun() { public void run() {
//			dropmenu_file.metode("clear_entrys");
//			app.addEventNextFrame(new nRun() { public void run() {
//				for (int k = 0; k < nInterface.codeKey.length; k++) 
//							if (nInterface.codeShow[k]) {
//					String sc = nInterface.codeKey[k];
//					nWidget w1 = (nWidget)dropmenu_file
//							.metodeGet("add_entry_custom", sc, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun(k) { public void run() {
//						
//						codesel_drop.setText(sc); 
//
//						arg1_field.setText("");
//						arg2_field.setText("");
//						arg3_field.setText("");
//						arg4_field.setText("");
//						
//						add_com_trig.show();
//						
//						int a = (Integer)builder;
//						arg1_label.setText(nInterface.codeArgs[a][0]);
//						if (arg1_label.getText().equals("T")) {
//							arg1_valsel_drop.hide();
//							arg1_field.setSX(RS*3f).show();
//							arg1_w_slide.hide();
//							arg1_label.show();
//							arg1_metsel_drop.hide();
//							arg1_bldsel_drop.hide();
//							arg1_paramsel_drop.hide();
//						} else if (arg1_label.getText().equals("V")) {
//							arg1_valsel_drop.show();
//							arg1_field.setSX(RS*3f).show();
//							arg1_w_slide.hide();
//							arg1_label.show();
//							arg1_metsel_drop.hide();
//							arg1_bldsel_drop.hide();
//							arg1_paramsel_drop.hide();
//						} else if (arg1_label.getText().equals("M")) {
//							arg1_valsel_drop.hide();
//							arg1_field.setSX(RS*3f).show();
//							arg1_w_slide.hide();
//							arg1_label.show();
//							arg1_metsel_drop.show();
//							arg1_bldsel_drop.hide();
//							arg1_paramsel_drop.hide();
//						} else if (arg1_label.getText().equals("B")) {
//							arg1_valsel_drop.hide();
//							arg1_field.setSX(RS*3f).show();
//							arg1_w_slide.hide();
//							arg1_label.show();
//							arg1_metsel_drop.hide();
//							arg1_bldsel_drop.show();
//							arg1_paramsel_drop.hide();
//						} else if (arg1_label.getText().equals("W")) {
//							arg1_valsel_drop.hide();
//							arg1_field.hide();
//							arg1_w_slide.show();
//							arg1_label.show();
//							arg1_metsel_drop.hide();
//							arg1_bldsel_drop.hide();
//							arg1_w_slide.setSliderVal(1).setText("W:10");
//							arg1_field.setText("10");
//							arg1_paramsel_drop.hide();
//						} else if (arg1_label.getText().equals("P")) {
//							arg1_valsel_drop.hide();
//							arg1_field.setSX(RS*3f).show();
//							arg1_w_slide.hide();
//							arg1_label.show();
//							arg1_metsel_drop.hide();
//							arg1_bldsel_drop.hide();
//							arg1_paramsel_drop.show();
//						} else if (arg1_label.getText().equals("")) {
//							arg1_valsel_drop.hide();
//							arg1_field.hide();
//							arg1_w_slide.hide();
//							arg1_label.hide();
//							arg1_metsel_drop.hide();
//							arg1_bldsel_drop.hide();
//							arg1_paramsel_drop.hide();
//						} else {
//							arg1_valsel_drop.hide();
//							arg1_field.setSX(RS*4f).show();
//							arg1_w_slide.hide();
//							arg1_label.show();
//							arg1_metsel_drop.hide();
//							arg1_bldsel_drop.hide();
//							arg1_paramsel_drop.hide();
//						}
//						
//						arg2_label.setText(nInterface.codeArgs[a][1]);
//						if (arg2_label.getText().equals("T")) {
//							arg2_valsel_drop.hide();
//							arg2_field.setSX(RS*3f).show();
//							arg2_w_slide.hide();
//							arg2_label.show();
//							arg2_metsel_drop.hide();
//							arg2_bldsel_drop.hide();
//							arg2_paramsel_drop.hide();
//						} else if (arg2_label.getText().equals("V")) {
//							arg2_valsel_drop.show();
//							arg2_field.setSX(RS*3f).show();
//							arg2_w_slide.hide();
//							arg2_label.show();
//							arg2_metsel_drop.hide();
//							arg2_bldsel_drop.hide();
//							arg2_paramsel_drop.hide();
//						} else if (arg2_label.getText().equals("M")) {
//							arg2_valsel_drop.hide();
//							arg2_field.setSX(RS*3f).show();
//							arg2_w_slide.hide();
//							arg2_label.show();
//							arg2_metsel_drop.show();
//							arg2_bldsel_drop.hide();
//							arg2_paramsel_drop.hide();
//						} else if (arg2_label.getText().equals("B")) {
//							arg2_valsel_drop.hide();
//							arg2_field.setSX(RS*3f).show();
//							arg2_w_slide.hide();
//							arg2_label.show();
//							arg2_metsel_drop.hide();
//							arg2_bldsel_drop.show();
//							arg2_paramsel_drop.hide();
//						} else if (arg2_label.getText().equals("W")) {
//							arg2_valsel_drop.hide();
//							arg2_field.hide();
//							arg2_w_slide.show();
//							arg2_label.show();
//							arg2_metsel_drop.hide();
//							arg2_bldsel_drop.hide();
//							arg2_w_slide.setSliderVal(1).setText("W:10");
//							arg2_field.setText("10");
//							arg2_paramsel_drop.hide();
//						} else if (arg2_label.getText().equals("P")) {
//							arg2_valsel_drop.hide();
//							arg2_field.setSX(RS*3f).show();
//							arg2_w_slide.hide();
//							arg2_label.show();
//							arg2_metsel_drop.hide();
//							arg2_bldsel_drop.hide();
//							arg2_paramsel_drop.show();
//						} else if (arg2_label.getText().equals("")) {
//							arg2_valsel_drop.hide();
//							arg2_field.hide();
//							arg2_w_slide.hide();
//							arg2_label.hide();
//							arg2_metsel_drop.hide();
//							arg2_bldsel_drop.hide();
//							arg2_paramsel_drop.hide();
//						} else {
//							arg2_valsel_drop.hide();
//							arg2_field.setSX(RS*4f).show();
//							arg2_w_slide.hide();
//							arg2_label.show();
//							arg2_metsel_drop.hide();
//							arg2_bldsel_drop.hide();
//							arg2_paramsel_drop.hide();
//						}
//						
//						arg3_label.setText(nInterface.codeArgs[a][2]);
//						if (arg3_label.getText().equals("T")) {
//							arg3_valsel_drop.hide();
//							arg3_field.setSX(RS*3f).show();
//							arg3_w_slide.hide();
//							arg3_label.show();
//							arg3_metsel_drop.hide();
//							arg3_bldsel_drop.hide();
//							arg3_paramsel_drop.hide();
//						} else if (arg3_label.getText().equals("V")) {
//							arg3_valsel_drop.show();
//							arg3_field.setSX(RS*3f).show();
//							arg3_w_slide.hide();
//							arg3_label.show();
//							arg3_metsel_drop.hide();
//							arg3_bldsel_drop.hide();
//							arg3_paramsel_drop.hide();
//						} else if (arg3_label.getText().equals("M")) {
//							arg3_valsel_drop.hide();
//							arg3_field.setSX(RS*3f).show();
//							arg3_w_slide.hide();
//							arg3_label.show();
//							arg3_metsel_drop.show();
//							arg3_bldsel_drop.hide();
//							arg3_paramsel_drop.hide();
//						} else if (arg3_label.getText().equals("B")) {
//							arg3_valsel_drop.hide();
//							arg3_field.setSX(RS*3f).show();
//							arg3_w_slide.hide();
//							arg3_label.show();
//							arg3_metsel_drop.hide();
//							arg3_bldsel_drop.show();
//							arg3_paramsel_drop.hide();
//						} else if (arg3_label.getText().equals("W")) {
//							arg3_valsel_drop.hide();
//							arg3_field.hide();
//							arg3_w_slide.show();
//							arg3_label.show();
//							arg3_metsel_drop.hide();
//							arg3_bldsel_drop.hide();
//							arg3_w_slide.setSliderVal(1).setText("W:10");
//							arg3_field.setText("10");
//							arg3_paramsel_drop.hide();
//						} else if (arg3_label.getText().equals("P")) {
//							arg3_valsel_drop.hide();
//							arg3_field.setSX(RS*3f).show();
//							arg3_w_slide.hide();
//							arg3_label.show();
//							arg3_metsel_drop.hide();
//							arg3_bldsel_drop.hide();
//							arg3_paramsel_drop.show();
//						} else if (arg3_label.getText().equals("")) {
//							arg3_valsel_drop.hide();
//							arg3_field.hide();
//							arg3_w_slide.hide();
//							arg3_label.hide();
//							arg3_metsel_drop.hide();
//							arg3_bldsel_drop.hide();
//							arg3_paramsel_drop.hide();
//						} else {
//							arg3_valsel_drop.hide();
//							arg3_field.setSX(RS*4f).show();
//							arg3_w_slide.hide();
//							arg3_label.show();
//							arg3_metsel_drop.hide();
//							arg3_bldsel_drop.hide();
//							arg3_paramsel_drop.hide();
//						}
//						
//						arg4_label.setText(nInterface.codeArgs[a][3]);
//						if (arg4_label.getText().equals("T")) {
//							arg4_valsel_drop.hide();
//							arg4_field.setSX(RS*3f).show();
//							arg4_w_slide.hide();
//							arg4_label.show();
//							arg4_metsel_drop.hide();
//							arg4_bldsel_drop.hide();
//							arg4_paramsel_drop.hide();
//						} else if (arg4_label.getText().equals("V")) {
//							arg4_valsel_drop.show();
//							arg4_field.setSX(RS*3f).show();
//							arg4_w_slide.hide();
//							arg4_label.show();
//							arg4_metsel_drop.hide();
//							arg4_bldsel_drop.hide();
//							arg4_paramsel_drop.hide();
//						} else if (arg4_label.getText().equals("M")) {
//							arg4_valsel_drop.hide();
//							arg4_field.setSX(RS*3f).show();
//							arg4_w_slide.hide();
//							arg4_label.show();
//							arg4_metsel_drop.show();
//							arg4_bldsel_drop.hide();
//							arg4_paramsel_drop.hide();
//						} else if (arg4_label.getText().equals("B")) {
//							arg4_valsel_drop.hide();
//							arg4_field.setSX(RS*3f).show();
//							arg4_w_slide.hide();
//							arg4_label.show();
//							arg4_metsel_drop.hide();
//							arg4_bldsel_drop.show();
//							arg4_paramsel_drop.hide();
//						} else if (arg4_label.getText().equals("W")) {
//							arg4_valsel_drop.hide();
//							arg4_field.hide();
//							arg4_w_slide.show();
//							arg4_label.show();
//							arg4_metsel_drop.hide();
//							arg4_bldsel_drop.hide();
//							arg4_w_slide.setSliderVal(1).setText("W:10");
//							arg4_field.setText("10");
//							arg4_paramsel_drop.hide();
//						} else if (arg4_label.getText().equals("P")) {
//							arg4_valsel_drop.hide();
//							arg4_field.setSX(RS*3f).show();
//							arg4_w_slide.hide();
//							arg4_label.show();
//							arg4_metsel_drop.hide();
//							arg4_bldsel_drop.hide();
//							arg4_paramsel_drop.show();
//						} else if (arg4_label.getText().equals("")) {
//							arg4_valsel_drop.hide();
//							arg4_field.hide();
//							arg4_w_slide.hide();
//							arg4_label.hide();
//							arg4_metsel_drop.hide();
//							arg4_bldsel_drop.hide();
//							arg4_paramsel_drop.hide();
//						} else {
//							arg4_valsel_drop.hide();
//							arg4_field.setSX(RS*4f).show();
//							arg4_w_slide.hide();
//							arg4_label.show();
//							arg4_metsel_drop.hide();
//							arg4_bldsel_drop.hide();
//							arg4_paramsel_drop.hide();
//						}
//						
//					}}); }
////				app.addEventNextFrame(new nRunnable() { public void run() {
//					dropmenu_file.metode("open", codesel_drop); 
////				}});
//			}});
//		}});
//		
//		build_interf.add_col_separator();
//
//		
//		//		      BUILD COMMAND LIST + MODEL MANAGEMENT
//
//		nWidgetGroup cg = command_interf.add_collapse_col("commandes");
////		cg.get("head").setOff();
//		
//		ArrayList<nInterfModel> all_mod  = new ArrayList<nInterfModel>();
//		g.addObject("selected_model_index", 0);
//		g.addObject("all_models", all_mod);
//		
//		command_interf.add_col_label("build models :");
//		command_interf.add_row();
//		nWidget select_model_ref = command_interf.add_row_label(7, "");
//		select_model_ref.setOutline(true);
//		
//		command_interf.add_row_trigg(1, "<", new nRun() { public void run() {
//			int selected_model_index = g.object("selected_model_index", Integer.class);
//			ArrayList<nInterfModel> all_models = 
//					g.object("all_models", ArrayList.class);
//			if (selected_model_index > 0) selected_model_index--; 
//			if (selected_model_index < all_models.size()) 
//				select_model_ref.setText(all_models.get(selected_model_index).ref); 
//			g.setObject("selected_model_index", selected_model_index);
//		}});
//		command_interf.add_row_trigg(1, ">", new nRun() { public void run() {
//			int selected_model_index = g.object("selected_model_index", Integer.class);
//			ArrayList<nInterfModel> all_models = 
//					g.object("all_models", ArrayList.class);
//			if (selected_model_index < all_models.size() - 1) 
//				selected_model_index++; 
//			if (selected_model_index < all_models.size()) 
//				select_model_ref.setText(all_models.get(selected_model_index).ref); 
//			g.setObject("selected_model_index", selected_model_index);
//		}});
//		command_interf.add_row_trigg(1, "x", new nRun() { public void run() {
//			int selected_model_index = g.object("selected_model_index", Integer.class);
//			ArrayList<nInterfModel> all_models = 
//					g.object("all_models", ArrayList.class);
//			if (selected_model_index < all_models.size()) 
//				all_models.remove(all_models.get(selected_model_index));
//			select_model_ref.setText("");
//			selected_model_index--;
//			while (selected_model_index >= all_models.size())
//				selected_model_index--;
//			if (selected_model_index < 0) selected_model_index = 0;
//			if (selected_model_index < all_models.size()) 
//				select_model_ref.setText(all_models.get(selected_model_index).ref); 
//			g.setObject("selected_model_index", selected_model_index);
//		}});
//
//		command_interf.add_col_separator();
//		
//		command_interf.add_col_label("new build model name :");
//		command_interf.add_row();
//		nWidget model_field = command_interf.add_row_field(10, "");
//		
//		command_interf.add_row();
//		command_interf.add_row_trigg(3, "save model", new nRun() { public void run() {
////			nInterfModel m = building.create_model(model_field.getText());
////			all_models.add(m);
////			selected_model_index = all_models.size() - 1;
////			select_model_ref.setText(all_models.get(selected_model_index).ref);
//		}});
//		command_interf.add_row_trigg(3, "append model", new nRun() { public void run() {
//			
//		}});
//		command_interf.add_row_trigg(3, "load model", new nRun() { public void run() {
////			if (all_models.size() > selected_model_index) {
////				nInterfModel m = all_models.get(selected_model_index);
////				building.build_from_model(m);
////				
////				app.addDelayEvent(7, new nRunnable() { public void run() {
////					building.ref.setParent(building_window.get("back"));
////				}});
////				
////				model_field.setText(m.ref);
////			}
//		}});
//		
//		
//
//		
//		command_interf.add_col_separator();
//		
//		command_interf.add_col_label("build commands :");
//		nWidget list_w = command_interf.add_col_entry();
//		list_w.setBoundChild(true);
//		nWidgetGroup list = g.addWidgetGroup("command_list_group", "scrollist");
//		list.get("ref")
//		.setParent(list_w)
//		.setBoundParent(true)
//		.setOutline(true);
//		list.metode("set_height", RS*4f);
//		
//		
//		
//		if (context_bloc != null ) {
//			sTab val_quickt_interf_model_default = 
//					context_bloc.getValue("val_quickt_interf_model_default", sTab.class);
//			
//			if (val_quickt_interf_model_default != null 
//					 && val_quickt_interf_model_default.width() > 0
//					) {
//				command_interf.add_row();
//				command_interf.add_row_trigg(6, "load default model", new nRun() { public void run() {
//					main_interf.build_from_sTab(val_quickt_interf_model_default);
//				}});
//			}
//		}
//		
//		
//		nRun run_minterf_newcom = new nRun() { public void run() {
//			app.addDelayEvent(3, new nRun() { public void run() {
//				if (!g.clearing) {
//					list.metode("clear_entrys");
//					int co_index = 0;
//					for (nInterfCommand co : main_interf.build_commands) {
//						nWidgetGroup entry = 
//								g.gui.addWidgetGroup("blocinterf_command_list_entry");
//						entry.metode("set_command", co, co_index, main_interf);
//						co_index++;
//						list.metodeGet("add_widget_as_entry", 
//								g.addWidget(
//								"entry_"+g.getGroup("command_list_group")
//								.getGroup("list").get("back").getChildNb(), 
//								entry.get("entry")));
//						if (co_index == 
//								main_interf.build_commands.size() - 
//								main_interf.command_addition_index) {
//							nWidget sep = (nWidget)list.metodeGet("add_entry", "-----");
//							sep.setSY(RS / 4f);
//						}
//					}
//				}
//			}});
//		}};
//		main_interf.addMetode("run_minterf_newcom", run_minterf_newcom);
//		
//		main_interf.addEventNewCommand(run_minterf_newcom);
//
//	}
//	
//	public static void build_blocinterf(Applet app) {
//		nModelBook book = app.gui.book;
//		float RS = book.RS;
//		
//		blocinterf_builder = new sBloc_Builder(app.data, "blocinterf")
//			.setSolo(false)
//			.setInitRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o;
//				nWidgetGroup win = app.gui.addWidgetGroup("blocinterf");
//				b.addObject("blocinterf_win", win);
//				win.metode("link_window_to_bloc", b);
//				win.metode("set_interfbloc", b);
//				win.addEventClear(new nRun() { public void run() {
//					b.clear(); }});
//				b.addMetode("blocinterf_tofront", new nRun() { public void run() {
//					win.metode("run_tofront"); }});
//				
//				win.metode("run_tofront");
//			}})
//			.setClearRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o;
//				nWidgetGroup w = b.object("blocinterf_win", nWidgetGroup.class);
//				if (w != null) w.clear();
//			}});
//		
//		app.addRootBlocBuilder(blocinterf_builder);
//		
//		book.newModelGroup("blocinterf_command_list_entry", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup();
//				nWidget ent = g.addWidget("entry", "list_entry");
//				nWidget title = g.addWidget("title", "list_entry_label_7");
//				nWidget point = g.addWidget("point", "list_entry_trigg_1");
//				nWidget del = g.addWidget("del", "list_entry_trigg_1");
//				title.setParent(ent).setSize(RS*7.8f, RS*0.6f);
//				point.setParent(ent).setSize(RS*0.6f, RS*0.6f).setText("_")
//				.set_color_pressed(app.color(170))
//				.set_color_hovered(app.color(140))
//				.set_color_standby(app.color(70))
//				;
//				del.setParent(ent).setSize(RS*0.6f, RS*0.6f).setText("x")
//				.set_color_pressed(app.color(170))
//				.set_color_hovered(app.color(140))
//				.set_color_standby(app.color(70))
//				;
//				ent.setSY(RS*0.6f).setOutline(false);
//				g.addMetode("set_command", new nRun() { 
//					public void run(Object o1, Object o2, Object o3) {
//					g.addObject("val", o1);
//					nInterfCommand v = (nInterfCommand)o1;
//					g.addObject("ind", o2);
//					int ind = (Integer)o2;
//					nInterface interf = (nInterface)o3;
//					String t = v.code + "";
//					if (v.args != null) for (int i = 0 ; i < v.args.length ; i++) 
//						t += " " + v.args[i];
//					title.setText(t); 
//					point.addEventTrigger(new nRun() { public void run() {
//						interf.setAdditionIndex(interf.build_commands.size() - ind - 1);
//						interf.metode("run_minterf_newcom");
//					}});
//					del.addEventTrigger(new nRun() { public void run() {
//						nInterfCommand c = interf.build_commands.get(ind);
//						interf.build_commands.remove(c);
//						interf.rebuild_from_command_list();
//					}});
//				}});
//				return g;
//			} 
//		} );
//		
//		book.newModelGroup("blocinterf", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup("complex_window");
//
//				nInterface back_interf = app.gui.addInterface()
//						.pop(g);
//
//				back_interf.add_col();
//				nWidget c1 = back_interf.current_col;
//				
//				back_interf.add_row();
//				back_interf.add_row_label(9,"");
//				nWidget mod_view_w = back_interf.add_row_switch(1,"E");
////				mod_view_w
////					.setBoundParent(false)
////					.setStacked(false)
////					;
//				
////				nWidget mod_view_w = g.addWidget("mod_view_w", "INT_row_entry_1");
////				mod_view_w.setParent(back_interf.current_row);
////				mod_view_w.setPos(RS*4f, -RS);
//				mod_view_w.setOff();
//				
//				back_interf.add_col();
//				nWidget c2 = back_interf.current_col;
//				c2.setVisibility(true);
//				
//				mod_view_w.addEventSwitch(new nRun(mod_view_w) { public void run() {
//					c2.setVisibility(((nWidget)builder).isOn()); }});
//				
//				nInterface main_interf = app.gui.addInterface()
//						.pop(c1);
//				
//				build_interf_editor(app, g, c2, main_interf, null);
//				
//				main_interf.add_line();
//				main_interf.add_col();
//				
//				g.addMetode("set_interfbloc", new nRun() {
//					public void run(Object o) {
//						if (o instanceof sValueBloc) {
//							sValueBloc b = (sValueBloc)o;
//							
//							g.addObject("interf_bloc", b);
//							
//							sValueBloc par = b.parent;
//							if (par != null) {
//								g.addObject("context_bloc", par);
//								g.metode("set_title", "interf : "+par.ref);
//								main_interf.setContext(par);
//								main_interf.rebuild_from_command_list();
//							}
//							
//							sTab val_interf_model = b.obtainTab("val_interf_model");
//							g.addObject("val_interf_model", val_interf_model);
//							
//							nInterfModel int_mod = new nInterfModel("interf_model");
//							int_mod.load_from(val_interf_model);
//							main_interf.build_from_model(int_mod);
//							
//							main_interf.addEventNewCommand(
//								new nRun() { public void run() {
//									nInterfModel im = main_interf
//											.create_model("interf_model");
//									im.save_to(val_interf_model);
//									val_interf_model.cancelChange();
//								}});
//							val_interf_model.addEventChangeThisFrame(
//								new nRun() { public void run() {
//									int_mod.load_from(val_interf_model);
//									main_interf.build_from_model(int_mod);
//								}});
//						}
//					}
//				});
//				
//				return g;
//			} 
//		} );
//		
//	}
		
	
	
	
	
	
//	//-------------------------------------//
//	//-         BLOC MENU                  //
//	//-------------------------------------//
//	
//	public static void build_blocmenu(Applet app) {
//		nModelBook book = app.gui.book;
//		float RS = book.RS;
//
//		book.newModel("BM_quicktoolref")
//		.setBoundChild(true)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(0)
//		.setStackSpacing(0)
//		;
//		
//		blocmenu_builder = new sBloc_Builder(app.data, "blocmenu")
//			.setSolo(true)
//			.setInitRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o;
//				nWidgetGroup win = app.gui.addWidgetGroup("blocmenu");
//				b.addObject("blocmenu_win", win);
//				win.metode("link_tabwindow_to_bloc", b);
//				win.metode("view_bloc", b.parent);
//				win.addEventClear(new nRun() { public void run() {
//					b.clear(); }});
//				b.addMetode("blocmenu_tofront", new nRun() { public void run() {
//					win.metode("run_tofront"); }});
//				
////				win.metode("run_tofront");
//			}})
//			.setClearRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o;
//				nWidgetGroup w = b.object("blocmenu_win", nWidgetGroup.class);
//				if (w != null) w.clear();
//			}})
//			.setBuilderAddRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o;
//				b.addMetode("add_menu", new nRun() { public void run() {
//					sValueBloc menu_bloc = b.getBloc("blocmenu");
//					if (menu_bloc == null) b.buildBloc("blocmenu", "blocmenu");
//				}});
//				b.addMetode("pop_menu", new nRun() { public void run() {
//					sValueBloc menu_bloc = b.getBloc("blocmenu");
//					if (menu_bloc == null) 
//						b.buildBloc("blocmenu", "blocmenu");
//					else menu_bloc.run("blocmenu_tofront");
//				}});
//			}});
//		
//		app.addCommonBlocBuilder(blocmenu_builder);
//		
//		book.newModelGroup("blocmenu", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup("tab_window");
//
//				nWidgetGroup main_tab = (nWidgetGroup)g.metodeGet("new_tab", "bloc");
//				nWidgetGroup run_tab = (nWidgetGroup)g.metodeGet("new_tab", "run");
//				nWidgetGroup val_tab = (nWidgetGroup)g.metodeGet("new_tab", "val");
//				nWidgetGroup tool_tab = (nWidgetGroup)g.metodeGet("new_tab", "tool");
//				
//				g.addMetode("view_bloc", new nRun() { public void run(Object o) {
//					if (o instanceof sValueBloc) {
//						sValueBloc b = (sValueBloc)o;
//						
//						g.addObject("viewed_bloc", b);
//						
//						g.metode("set_title", "menu: " + b.ref);
//
//						nWidget quicktool_interf_w = g.addWidget(
//								"quicktool_interf_w", "BM_quicktoolref");
//						if (app.menu.toolbox.toolbox_quicktool_sec == null) 
//							app.menu.toolbox.build_quicktool();
//						quicktool_interf_w.setParent(
//								app.menu.toolbox.toolbox_quicktool_sec.get("back")).hide();	
//						nInterface quicktool_interf = app.gui.addInterface()
//								.pop(quicktool_interf_w);
//						
//						b.addEventSelect(new nRun() { public void run() {
//							quicktool_interf_w.show(); 
////							if (g.getObject("val_collapse") != null && 
////									!g.getObject("val_collapse", sBoo.class).get()) 
////								g.metode("run_tofront");
//						}});
//						b.addEventUnselect(new nRun() { public void run() {
//							quicktool_interf_w.hide(); }});
//						
//						g.metode("set_tofront_event", 
//								new nRun() { public void run() {
//							b.select_bloc(); }});
//						
//						quicktool_interf.setContext(b);
//						
//						sTab val_quickt_interf_model = b.obtainTab("val_quickt_interf_model");
//						g.addObject("val_quickt_interf_model", val_quickt_interf_model);
//						sTab val_quickt_interf_model_default = b.obtainTab("val_quickt_interf_model_default");
//						g.addObject("val_quickt_interf_model_default", val_quickt_interf_model_default);
//
//						build_interf_editor(app, tool_tab, tool_tab.get("back"), 
//								quicktool_interf, b);
//						
//						nInterfModel int_mod = new nInterfModel("interf_model");
//						int_mod.load_from(val_quickt_interf_model);
//						quicktool_interf.build_from_model(int_mod);
//
//						quicktool_interf.addEventNewCommand(
//							new nRun() { public void run() {
//								nInterfModel im = quicktool_interf
//										.create_model("interf_model");
//								im.save_to(val_quickt_interf_model);
//								val_quickt_interf_model.cancelChange();
//							}});
//						val_quickt_interf_model.addEventChangeThisFrame(
//							new nRun() { public void run() {
//								int_mod.load_from(val_quickt_interf_model);
//								quicktool_interf.build_from_model(int_mod);
//							}});
//						
//						
//						
//						nInterface main_interf = app.gui.addInterface()
//								.pop(main_tab);
//						
//						main_interf.add_col();
//
//						main_interf.add_col_label("adress: "+b.adress);
//						
//						main_interf.add_row();
//						main_interf.add_row_label(9, "delete this bloc: ");
//						if (b.parent != null) main_interf.add_row_trigg(1, "X", 
//								new nRun() { public void run() { 
//									b.clear(); }});
//						
//						String s = "none";
//						if (b.parent != null) s = b.parent.ref;
//						main_interf.add_row();
//						main_interf.add_row_label(9, "parent: "+s);
//						if (b.parent != null) main_interf.add_row_trigg(1, "M", 
//								new nRun() { public void run() { 
//									 b.parent.run("pop_menu"); }});
//						
//						main_interf.add_col_label("child blocs :");
//						nWidget bloclist_w = main_interf.add_col_entry();
//						bloclist_w.setBoundChild(true);
//						
//						nWidgetGroup bloclist = gui.addWidgetGroup("scrollist");
//						g.addWidgetGroup("bloclist",bloclist);
//						bloclist.get("ref").setParent(bloclist_w);
//						bloclist.metode("set_height", RS*6f);
//						
//						nRun update_bloclist = new nRun() { public void run() {
//							bloclist.metode("clear_entrys");
//							if (b.blocs.size() > 0) {
//								for (Entry<String, sValueBloc> mev : b.blocs.entrySet()) {
//									bloclist.metodeGet("add_widget_as_entry", 
//										bloclist.addWidget(
//											"bloclist_entry_"+mev.getKey(), 
//											gui.addWidget("list_trigger", 
//													"open <"+mev.getKey()+"> menu")
//											.addEventTrigger(new nRun() {
//												public void run() {
//													mev.getValue().run("pop_menu"); }})
//										)
//									);
//								}
//							}
//						}};
//						update_bloclist.run();
//						b.addEventChangeThisFrame(new nRun() { public void run() {
//							if (!b.clearing) update_bloclist.run(); }});
//						
//						
//						nInterface run_interf = app.gui.addInterface()
//								.pop(run_tab);
//						
//						run_interf.add_col();
//						
//						run_interf.add_row();
//						run_interf.add_row_label(8,"Metodes :");
//						nWidget metodeview_w = run_interf.add_row_switch(2,"all");
//						nWidget metodelist_w = run_interf.add_col_entry();
//						metodelist_w.setBoundChild(true);
//						
//						nWidgetGroup metodelist = gui.addWidgetGroup("scrollist");
//						g.addWidgetGroup("metodelist",metodelist);
//						metodelist.get("ref").setParent(metodelist_w);
//						metodelist.metode("set_height", RS*5f);
//						
//						nRun update_metodelist = new nRun() { public void run() {
//							metodelist.metode("clear_entrys");
//							if (b.metodes.size() > 0) {
//								for (Entry<String, nRun> mev : b.metodes.entrySet()) 
//										if (metodeview_w.isOn() || 
//											b.metodes.isFlag(mev.getKey(), "view_in_menu")) {
//									metodelist.metodeGet("add_widget_as_entry", 
//										metodelist.addWidget(
//											"metodelist_entry_"+mev.getKey(), 
//											gui.addWidget("list_trigger", mev.getKey())
//											.addEventTrigger(new nRun() {
//												public void run() {
//													mev.getValue().run(); }})
//										)
//									);
//								}
//							}
//						}};
//						update_metodelist.run();
//						metodeview_w.addEventSwitch(new nRun() { public void run() {
//							update_metodelist.run(); }});
//						
//
//						run_interf.add_col_label("builder :");
//						nWidget buildlist_w = run_interf.add_col_entry();
//						buildlist_w.setBoundChild(true);
//						
//						nWidgetGroup buildlist = gui.addWidgetGroup("scrollist");
//						g.addWidgetGroup("buildlist",buildlist);
//						buildlist.get("ref").setParent(buildlist_w);
//						buildlist.metode("set_height", RS*5f);
//						
//						
//						
//						nRun update_builderlist = new nRun() { public void run() {
//							buildlist.metode("clear_entrys");
//							if (b.bloc_builders.size() > 0) {
//								for (int i = 0 ; i < b.bloc_builders_types.size() ; i++) 
//								if (b.bloc_builders.get(i).isBuildableIn(b)) {
//									String buildertype = b.bloc_builders_types.get(i);
//									buildlist.metodeGet("add_widget_as_entry", 
//										buildlist.addWidget(
//											"buildlist_entry_" + buildertype, 
//											gui.addWidget("list_trigger", buildertype)
//											.addEventTrigger(new nRun() { public void run() {
//												b.buildBloc(buildertype,buildertype); }})
//										)
//									);
//								}
//							}
//						}};
//						update_builderlist.run();
//						b.addEventChangeThisFrame(new nRun() { public void run() {
//							if (!b.clearing) update_builderlist.run(); }});
//						
//
//						
//						nInterface val_interf = app.gui.addInterface()
//								.pop(val_tab);
//						
//						val_interf.add_col();
//						
//						val_interf.add_col_label("values :");
//						nWidget vallist_w = val_interf.add_col_entry();
//						vallist_w.setBoundChild(true);
//						
//						nWidgetGroup vallist = gui.addWidgetGroup("scrollist");
//						g.addWidgetGroup("vallist",vallist);
//						vallist.get("ref").setParent(vallist_w);
//						vallist.metode("set_height", RS*6f);
//						
//						if (b.values.size() > 0) {
//							for (Entry<String, sValue> mev : b.values.entrySet()) {
//								String val_text = mev.getValue().type + " : " + 
//										mev.getKey() + " : ";
//								vallist.metodeGet("add_widget_as_entry", 
//									vallist.addWidget(
//										"vallist_entry_"+mev.getKey(), 
//										gui.addWidget("list_entry")
//											.setWatcher(val_text, mev.getValue(), "")
//									)
//								);
//							}
//						}
//						
//						
//						if (b.builder != null) {
//							val_interf.add_col_separator();
//							val_interf.add_col_label("Values Presets :");
//							
//							val_interf.add_row();
//							nWidget model_add_trigg = 
//									val_interf.add_row_trigg(4, "SAVE AS");
//							nWidget model_field = val_interf.add_row_field(6, "");
//							val_interf.add_row();
//							nWidget model_sel_label = 
//									val_interf.add_row_label(6, "");
//							nWidget model_load_trigg = 
//									val_interf.add_row_trigg(2, "LOAD");
//							val_interf.add_row_label(1, "");
//							nWidget model_del_trigg = 
//									val_interf.add_row_trigg(1, "DEL");
//							nWidget presetlist_w = val_interf.add_col_entry();
//							presetlist_w.setBoundChild(true);
//							nWidgetGroup presetlist = gui.addWidgetGroup("picklist");
//							g.addWidgetGroup("presetlist",presetlist);
//							presetlist.get("ref").setParent(presetlist_w);
//							presetlist.metode("set_height", RS*4f);
//							
//							nRun update_modellist = new nRun() { 
//									public void run() { 
//								presetlist.metode("clear_entrys");
//								model_sel_label.setText("");
//								for (Entry<String, sValueBloc> mev : 
//									b.builder.preset_bloc.blocs.entrySet()) {
//									nWidget pic = (nWidget)presetlist
//										.metodeGet("add_pick", mev.getKey());
//									pic.addEventSwitchOn(new nRun() { public void run() {
//										model_sel_label.setText(mev.getKey());
//									}});
//								}
//							}};
//							update_modellist.run();
//							
//							b.builder.preset_bloc.addEventChangeThisFrame(
//								new nRun() { public void run() {
//									update_modellist.run(); }});
//							
//							model_add_trigg.addEventTrigger(
//								new nRun() { public void run() { 
//									if (b.builder.preset_bloc
//											.getBloc(model_field.getText()) == null) {
//										sValueBloc nb = b.data.copy_bloc_value(
//												b, b.builder.preset_bloc, 
//												model_field.getText());
//	//									update_modellist.run();
//									}
//								}});
//							model_load_trigg.addEventTrigger(
//								new nRun() { public void run() { 
//									sValueBloc modelbloc = 
//										b.builder.preset_bloc.getBloc(
//										model_sel_label.getText());
//									if (modelbloc != null) {
//										b.data.transfer_bloc_values(
//											modelbloc, b);
//									}
//								}});
//							model_del_trigg.addEventTrigger(
//								new nRun() { public void run() { 
//									sValueBloc modelbloc = 
//										b.builder.preset_bloc.getBloc(
//										model_sel_label.getText());
//									if (modelbloc != null) {
//										modelbloc.clear();
//	//									update_modellist.run();
//									}
//								}});
//							
//						}
//					}
//				} });
//				
//				return g;
//			} 
//		} );
//		
//	}
	
	
	
	
	
//	//-------------------------------------//
//	//-         DATA VIEW                  //
//	//-------------------------------------//
//		
//	
//	public static void build_dataview(Applet app) {
//		nModelBook book = app.gui.book;
//		float RS = book.RS;
//		
//		dataview_builder = new sBloc_Builder(app.data, "dataview")
//			.setSolo(true)
//			.setInitRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o;
//				
//				app.data.dataview_bloc = b;
//				
//				nWidgetGroup view = app.gui.addWidgetGroup("dataview");
////				view.get("head").setText("DATA VIEW");
//				b.addObject("viewgroup", view);
//				view.metode("set_title", "DATA VIEW");
//				view.metode("link_to_bloc", b);
//
//				nWidget close = view.get("close");
//				close.addEventTrigger(new nRun() { public void run() {
//					b.clear(); }});
//
//				nRun change_run = new nRun() { public void run() {
//					view.metode("populate", app.data); }};
//				b.addMetode("populate_run", change_run);
//				
//				b.addMetode("center_on_bloc", 
//						new nRun() { public void run(Object o1) {
//					sValueBloc bloc = (sValueBloc)o1;
//					nWidgetGroup box_in_dataview = bloc.box_in_dataview;
//					if (box_in_dataview != null) {
//						nWidget ref = box_in_dataview.get("ref");
//						if (ref.getSX() > 0 && ref.getSY() > 0) {
//							nWidget dataref = view.get("dataref");
//							sVec vs = b.getValue("val_view_size", sVec.class);
//							sVec vp = b.getValue("val_cam_pos", sVec.class);
//							sFlt vz = b.getValue("val_cam_scale", sFlt.class);
//							if (vs != null && vp != null && vz != null) {
//								Vector2 p = ref.getPosRelativeToParent(dataref);
//								float neededscalex = vs.x() / (
//										ref.getBoundedSize().x * 1.1f);
//								float neededscaley = vs.y() / (
//										ref.getBoundedSize().y * 1.1f);
//								vz.set(Math.min(neededscalex, neededscaley));
//								vp.set(-p.x - ref.getBoundedSize().x / 2f, 
//										-p.y - ref.getBoundedSize().y / 2f);
//							}
//						}
//					}
//				}});
//				
//				nWidget dezoom = view.addWidget("dezoom", "DV_trigg");
//				dezoom.setText("dezoom").setRect(50,120, 80, 30);
//				dezoom.addEventTrigger(new nRun() { public void run() {
//					nWidget dataref = view.get("dataref");
//					sVec vs = b.getValue("val_view_size", sVec.class);
//					sVec vp = b.getValue("val_cam_pos", sVec.class);
//					sFlt vz = b.getValue("val_cam_scale", sFlt.class);
//					if (vs != null && vp != null && vz != null) {
//						float neededscalex = vs.x() / (
//								dataref.getBoundedSize().x * 1.1f);
//						float neededscaley = vs.y() / (
//								dataref.getBoundedSize().y * 1.1f);
//						vz.set(Math.min(neededscalex, neededscaley));
//						vp.set(-dataref.getBoundedSize().x / 2f, 
//								-dataref.getBoundedSize().y / 2f);
//					}
//				}});
//				view.metode("add_widget_to_front", dezoom);
//				
//				nWidget fullscr = view.addWidget("fullscr", "DV_trigg");
//				fullscr.setText("fullscr").setRect(50,120, 80, 30);
//				fullscr.addEventTrigger(new nRun() { public void run() {
//					sVec vs = b.getValue("val_view_size", sVec.class);
//					sVec vp = b.getValue("val_pos", sVec.class);
//					if (vs != null && vp != null) {
//						vp.set(0, Applet.HEIGHT - RS - 10);
//						vs.set(Applet.WIDTH, Applet.HEIGHT - 3*RS - 20);
//						app.addEventNextFrame(new nRun() { public void run() {
//							view.metode("event_corner_drag"); }});
//					}
//				}});
//				view.metode("add_widget_to_front", fullscr);
//				
//				b.addEventDelete(new nRun() { public void run() {
//					app.data.removeEventAllChange(change_run); 
//					view.clear(); 
//					app.data.dataview_bloc = null; }});
//				
//				app.addDelayEvent(1, new nRun() { public void run() {
//					app.data.addEventAllChange(change_run); 
//					change_run.run(); }});
//			}})
//			.setClearRun(new nRun() { public void run(Object o) {}});
//		
//		app.addRootBlocBuilder(dataview_builder);
//		
//		book.newModel("DV_dataref")
//		.setBackground()
//		.set_color_background(app.color(0,0))
//		.setBoundChild(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(RS)
//		.setStackSpacing(RS/2f)
//		.set_color_outline(app.color(0,0,190))
//		.set_color_outline_selected(app.color(200,200,0))
//		.setOutline(true)
//		.setOutlineWeight(RS / 30f)
//		;
//		
//		book.newModel("DV_bloc")
//		.setBackground()
//		.set_color_background(app.color(80,110))
//		.setBoundChild(true)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(RS/4f)
//		.setStackSpacing(RS/2f)
//		.set_color_outline(app.color(200,200,200))
//		.set_color_outline_selected(app.color(200,200,0))
//		.setOutline(true)
//		.setOutlineWeight(RS / 5f)
//		.setSelectable(true)
//		.setOutlineAfterChild(true)
//		;
//		
//		book.newModel("DV_box")
//		.set_color_background(app.color(50,110))
//		.setBoundChild(true)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(RS/10f)
//		.setStackSpacing(RS/2f)
//		.set_color_outline(app.color(200,200,200))
//		.set_color_outline_selected(app.color(200,200,0))
//		.setOutline(true)
//		.setOutlineWeight(RS / 10f)
//		.setOutlineAfterChild(true)
//		;
//		
//		book.newModel("DV_stack")
////		.setBackground()
//		.set_color_background(app.color(0,0))
//		.setOutline(false)
//		.setBoundChild(true)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(0)
//		.setStackSpacing(RS/3f)
//		;
//		book.newModel("DV_view")
////		.setBackground()
//		.set_color_background(app.color(0,0,0,0))
//		.setOutline(false)
//		.setBoundChild(true)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(0)
//		.setStackSpacing(0)
//		;
//
//		book.newModel("DV_row")
////		.setSize(RS*6, RS*2/3)
////		.setBackground()
//		.set_color_background(app.color(0,0))
//		.setBoundChild(true)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(0)
//		.setStackSpacing(0)
//		;
//		
//		book.newModel("DV_label")
//		.setSize(RS*6, RS*2/3)
////		.setBackground()
//		.set_color_background(app.color(0,0,0,0))
//		.setBoundParent(true)
//		.setStacked(true)
//		;
//		book.newModel("DV_trigg")
//		.setSize(RS*6, RS*2/3)
//		.setTrigger()
//		.setBoundParent(true)
//		.setStacked(true)
//		;
//		book.newModel("DV_collapse")
//		.setSize(RS*2/3, RS*2/3)
//		.setTrigger()
//		.setText("v")
//		;
//		
//		book.newModelGroup("dataview", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup("viewspace");
//
//				nWidget dataref = g.addWidget("dataref", gui.addWidget("DV_dataref")
//						.setParent(g.get("backref"))
//						);
//				
//				ArrayList<nWidgetGroup> entrys = 
//						new ArrayList<nWidgetGroup>();
//				
//				g.addObject("entrys", entrys);
//				
//				g.addMetode("empty", new nRun() {
//					public void run() {
//						for (nWidgetGroup e : entrys) {
//							e.clear();
//						}
//						entrys.clear();
//					}
//				});
//				g.addMetode("populate", new nRun() {
//					public void run(Object o) {
//						sValueBloc vb = ((sValueBloc)o);
//						g.metode("empty");
//						g.metode("populate", vb, dataref, false);
//					}
//					public void run(Object o1, Object o2, Object o3) {
//						sValueBloc vb = ((sValueBloc)o1);
//						nWidget parent = ((nWidget)o2);
//						boolean axe = ((boolean)o3);
//						
//						if (vb.metodes.size() > 0) {
//							nWidgetGroup blocm = 
//									gui.addWidgetGroup("dataview_box");
//							blocm.get("ref").set_color_outline(app.color(160,120,10));
//							entrys.add(blocm);
//							blocm.get("ref").setParent(parent);
//							blocm.get("ref").setStackSpacing(RS/3f);
//							blocm.metode("setAxe", false);
//							nWidget stack = blocm.addWidget("stack_0", "DV_stack");
//							stack.setStack(nAlign.VERTICAL, nAlign.DOWN)
//							.setStackSpacing(0); 
//							stack.setParent(blocm.get("ref"));
//							
//							blocm.addObject("stack_size", 0);
//							blocm.addObject("stack_max", 5);
//							blocm.addObject("stack_nb", 0);
//							
//							for (Entry<String, nRun> mev : vb.metodes.entrySet()) {
//
//								int stack_size = 1 +  
//										(Integer)blocm.object("stack_size");
//								int stack_max = 
//										(Integer)blocm.object("stack_max");
//								int stack_nb = 
//										(Integer)blocm.object("stack_nb");
//								if (stack_size > stack_max) {
//									stack_size = 0;
//									stack_nb++;
//									stack = blocm.addWidget(
//													"stack_"+stack_nb, "DV_stack");
//									stack.setStack(nAlign.VERTICAL, nAlign.DOWN)
//									.setStackSpacing(0); 
//									stack.setParent(blocm.get("ref"));
//								}
//								blocm.setObject("stack_size", stack_size);
//								blocm.setObject("stack_nb", stack_nb);
//								
//								nWidgetGroup entry = 
//										gui.addWidgetGroup("dataview_metode");
//								entrys.add(entry);
//								entry.metode("set", mev, 
//										blocm.get("stack_"+stack_nb));
//							}
//						}
//						if (vb.bloc_builders.size() > 0) {
//							nWidgetGroup blocb = 
//									gui.addWidgetGroup("dataview_box");
//							blocb.get("ref").set_color_outline(app.color(100,10,160));
//							entrys.add(blocb);
//							blocb.get("ref").setParent(parent);
//							blocb.get("ref").setStackSpacing(RS/3f);
//							blocb.metode("setAxe", false);
//							nWidget stack = blocb.addWidget("stack_0", "DV_stack");
//							stack.setStack(nAlign.VERTICAL, nAlign.DOWN)
//							.setStackSpacing(0); 
//							stack.setParent(blocb.get("ref"));
//							
//							blocb.addObject("stack_size", 0);
//							blocb.addObject("stack_max", 5);
//							blocb.addObject("stack_nb", 0);
//							
//							for (int i = 0 ; i < vb.bloc_builders_types.size() ; i++) {
//								
//								int stack_size = 1 +  
//										(Integer)blocb.object("stack_size");
//								int stack_max = 
//										(Integer)blocb.object("stack_max");
//								int stack_nb = 
//										(Integer)blocb.object("stack_nb");
//								if (stack_size > stack_max) {
//									stack_size = 0;
//									stack_nb++;
//									stack = blocb.addWidget(
//													"stack_"+stack_nb, "DV_stack");
//									stack.setStack(nAlign.VERTICAL, nAlign.DOWN)
//									.setStackSpacing(0); 
//									stack.setParent(blocb.get("ref"));
//								}
//								blocb.setObject("stack_size", stack_size);
//								blocb.setObject("stack_nb", stack_nb);
//								
//								nWidgetGroup entry = 
//										gui.addWidgetGroup("dataview_builder");
//								entrys.add(entry);
//								entry.metode("set", blocb.get("stack_"+stack_nb), 
//										vb.bloc_builders_types.get(i));
//								entry.metode("set_build", vb, 
//										vb.bloc_builders.get(i) );
//							}
//						}
//						
//						if (vb.values.size() > 0) {
//							nWidgetGroup blocval = 
//									gui.addWidgetGroup("dataview_box");
//							blocval.get("ref")
//							.set_color_outline(app.color(100,150,100));
//							entrys.add(blocval);
//							blocval.get("ref").setParent(parent);
//							blocval.get("ref").setStackSpacing(RS/3f);
//							blocval.metode("setAxe", false);
//							nWidget stack = blocval.addWidget("stack_0", "DV_stack");
//							stack.setStack(nAlign.VERTICAL, nAlign.DOWN)
//							.setStackSpacing(0); 
//							stack.setParent(blocval.get("ref"));
//							
//							blocval.addObject("stack_size", 0);
//							blocval.addObject("stack_max", 5);
//							blocval.addObject("stack_nb", 0);
//							
//							vb.runValueIterator(new nIterator<sValue>() {
//								public void run(sValue t) { 
//									
//									int stack_size = 1 +  
//											(Integer)blocval.object("stack_size");
//									int stack_max = 
//											(Integer)blocval.object("stack_max");
//									int stack_nb = 
//											(Integer)blocval.object("stack_nb");
//									if (stack_size > stack_max) {
//										stack_size = 0;
//										stack_nb++;
//										nWidget stack = 
//												blocval.addWidget(
//														"stack_"+stack_nb, "DV_stack");
//										stack.setStack(nAlign.VERTICAL, nAlign.DOWN)
//										.setStackSpacing(0); 
//										stack.setParent(blocval.get("ref"));
//									}
//									blocval.setObject("stack_size", stack_size);
//									blocval.setObject("stack_nb", stack_nb);
//									
//									nWidgetGroup entry = 
//											gui.addWidgetGroup("dataview_val");
//									entrys.add(entry);
//									entry.metode("set", t, 
//											blocval.get("stack_"+stack_nb));
//								}
//							});
//						}
//
//						if (vb.blocs.size() > 0) {
//							nWidgetGroup blocbloc = 
//									gui.addWidgetGroup("dataview_box");
//							blocbloc.get("ref").setParent(parent)
//							.set_color_background(app.color(50,255))
//							.setBoundOutspace(RS).setStackSpacing(RS).setOutline(false);
//							entrys.add(blocbloc);
//							blocbloc.metode("setAxe", axe);
//							nWidget stack = blocbloc.addWidget("stack_0", "DV_stack");
//							if (!axe) stack.setStack(nAlign.VERTICAL, nAlign.DOWN); 
//							else stack.setStack(nAlign.HORIZONTAL, nAlign.RIGHT);
//							stack.setParent(blocbloc.get("ref"));
//	
//							blocbloc.addObject("stack_size", 0);
//							blocbloc.addObject("stack_max", 5);
//							blocbloc.addObject("stack_nb", 0);
//							
//							vb.runBlocIterator(new nIterator<sValueBloc>() {
//								public void run(sValueBloc t) { 
//									int stack_size = 1 +  
//											(Integer)blocbloc.object("stack_size");
//									int stack_max = 
//											(Integer)blocbloc.object("stack_max");
//									int stack_nb = 
//											(Integer)blocbloc.object("stack_nb");
//									if (stack_size > stack_max) {
//										stack_size = 0;
//										stack_nb++;
//										nWidget stack = 
//												blocbloc.addWidget(
//														"stack_"+stack_nb, "DV_stack");
//										if (!axe) stack.setStack(
//												nAlign.VERTICAL, nAlign.DOWN); 
//										else stack.setStack(
//												nAlign.HORIZONTAL, nAlign.RIGHT);
//										stack.setParent(blocbloc.get("ref"));
//									}
//									blocbloc.setObject("stack_size", stack_size);
//									blocbloc.setObject("stack_nb", stack_nb);
//									
//									nWidgetGroup entry = 
//											gui.addWidgetGroup("dataview_bloc");
//									entry.get("ref").set_color_outline(app.color(0,0,150));
//									entry.get("view").setStackSpacing(RS/10f);
//									entrys.add(entry);
//									entry.metode("set", t, blocbloc.get("stack_"+stack_nb));
//									entry.metode("setAxe", axe);
//									t.box_in_dataview = entry;
//									g.metode("populate", t, entry.get("view"), !axe);
//								}
//							});
//						}
//					}
//				});
//
//				return g;
//			} 
//		} );
//		
//
//		
//		book.newModelGroup("dataview_box", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup();
//				
//				nWidget ent = g.addWidget("ref", "DV_box");
//				g.addMetode("setAxe", new nRun() {
//					public void run(Object o) {
//						boolean a = ((boolean)o);
//						if (a) ent.setStack(nAlign.VERTICAL, nAlign.DOWN); 
//						else ent.setStack(nAlign.HORIZONTAL, nAlign.RIGHT);
//					}});
//				return g;
//			} 
//		} );
//		
//		book.newModelGroup("dataview_bloc", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup();
//				
//				nWidget ent = g.addWidget("ref", "DV_bloc");
//				nWidget label = g.addWidget("label", "DV_label");
//				label.setParent(ent);
//				nWidget collapse = g.addWidget("collapse", "DV_collapse");
//				collapse.setParent(label);
//				nWidget view = g.addWidget("view", "DV_view");
//				view.setParent(ent);
//				
//				collapse.addEventTrigger(new nRun() { public void run() {
//					view.switchVisibility(); 
//					sValueBloc bl = g.object("bloc", sValueBloc.class);
//					if (bl != null) bl.open_in_dataview = view.getVisibility(); }});
//				
//				g.addMetode("set", new nRun() {
//					public void run(Object o1, Object o2) {
//						sValueBloc vb = ((sValueBloc)o1);
//						nWidget parent = ((nWidget)o2);
//						g.addObject("bloc", vb);
//						g.addObject("parent", parent);
//						ent.setParent(parent);
//						label.setText(vb.ref);
//						view.setVisibility(vb.open_in_dataview); 
//						
//						vb.addObject("dataview_bloc_ref", ent);
//						vb.addEventSelect(new nRun() { public void run() {
//							ent.select(); }});
//						vb.addEventUnselect(new nRun() { public void run() {
//							ent.unselect(); }});
//						ent.addEventSelect(new nRun() { public void run() {
//							vb.select_bloc(); }});
////						ent.addEventUnselect(new nRunnable() { public void run() {
////							vb.unselect_bloc(); }});
//						if (vb.is_selected()) ent.select();
//					}});
//				g.addMetode("setAxe", new nRun() {
//					public void run(Object o) {
//						boolean a = ((boolean)o);
//						if (a) view.setStack(nAlign.VERTICAL, nAlign.DOWN); 
//						else view.setStack(nAlign.HORIZONTAL, nAlign.RIGHT);
//					}});
//				return g;
//			} 
//		} );
//		
//		
//		book.newModelGroup("dataview_val", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup();
//				
//				nWidget ent = g.addWidget("ref", "DV_bloc");
//				ent.set_color_background(app.color(80,80,80,255));
//				ent.set_color_outline(app.color(100,150,100));
//				ent.setOutline(false);
//				ent.setBoundOutspace(0)
//				.setStackSpacing(RS/10f);
//				nWidget label = g.addWidget("label", "DV_label");
//				label.setParent(ent);
//				nWidget collapse = g.addWidget("collapse", "DV_collapse");
//				collapse.setParent(label);
//				nWidget view = g.addWidget("view", "DV_view");
//				view.setParent(ent).hide();
//				
//				collapse.addEventTrigger(new nRun() { public void run() {
//					view.switchVisibility(); }});
//				
//				nWidget label_val = g.addWidget("label_val", "DV_label");
//				label_val.setParent(view);
//
//				g.addMetode("set", new nRun() {
//					public void run(Object o1, Object o2) {
//						sValue vb = ((sValue)o1);
//						nWidget parent = ((nWidget)o2);
//						g.addObject("val", vb);
//						g.addObject("parent", parent);
//						ent.setParent(parent);
//						label.setText(vb.type + " : " + vb.ref);
//						label_val.setWatcher(vb);
//					}});
//				return g;
//			} 
//		} );
//
//		book.newModelGroup("dataview_builder", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup();
//				
//				nWidget ent = g.addWidget("ref", "DV_bloc");
//				ent.set_color_background(app.color(80,80,80,255));
//				ent.set_color_outline(app.color(100,10,160));
//				ent.setOutline(false);
//				ent.setBoundOutspace(0)
//				.setStackSpacing(RS/10f);
//				
//				nWidget row = g.addWidget("row", "DV_row");
//				row.setParent(ent);
//				nWidget label = g.addWidget("label", "DV_label");
//				label.setParent(row).setSize(RS*4f, RS*2f/3f);
//				nWidget build = g.addWidget("build", "DV_trigg");
//				build.setParent(row).setText("build")
//					.setSize(RS*2f, RS*2f/3f);
//				 
//				g.addMetode("set", new nRun() {
//					public void run(Object o1, Object o2) {
//						nWidget parent = ((nWidget)o1);
//						String t = ((String)o2);
//						g.addObject("parent", parent);
//						g.addObject("type", t);
//						ent.setParent(parent);
//						label.setText(t);
//					}});
//				g.addMetode("set_build", new nRun() {
//					public void run(Object o1, Object o2) {
//						sValueBloc bloc = ((sValueBloc)o1);
//						sBloc_Builder b = ((sBloc_Builder)o2);
//						g.addObject("builder", b);
//						g.addObject("bloc", bloc);
//						g.get("build").addEventTrigger(new nRun() { public void run() {
//							bloc.buildBloc(b, b.ref);
//						}});
//					}});
//				return g;
//			} 
//		} );
//		
//		book.newModelGroup("dataview_metode", new nModelGroup(app) { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup();
//				
//				nWidget ent = g.addWidget("ref", "DV_bloc");
//				ent.set_color_background(app.color(80,80,80,255));
//				ent.set_color_outline(app.color(160,120,10));
//				ent.setOutline(false);
//				ent.setBoundOutspace(0)
//				.setStackSpacing(RS/10f);
//				nWidget row = g.addWidget("row", "DV_row");
//				row.setParent(ent);
//				nWidget label = g.addWidget("label", "DV_label");
//				label.setParent(row).setSize(RS*4f, RS*2f/3f);
//				nWidget run = g.addWidget("run", "DV_trigg");
//				run.setParent(row).setText("run")
//					.setSize(RS*2f, RS*2f/3f);
//				 
//				g.addMetode("set", new nRun() {
//					public void run(Object o1, Object o2) {
//						Entry<String, nRun> mev = ((Entry<String, nRun>)o1);
//						nRun r = ((nRun)mev.getValue());
//						String k = ((String)mev.getKey());
//						nWidget parent = ((nWidget)o2);
//						g.addObject("val", mev);
//						g.addObject("parent", parent);
//						ent.setParent(parent);
//						label.setText(k);
//						run.addEventTrigger(r);
//					}});
//				return g;
//			} 
//		} );
//		
//	}
	
	
	
	
	
	//-------------------------------------//
	//-         EXPLORER                   //
	//-------------------------------------//	
		
	public static void build_explorer(Applet app) {

		nModelBook book = app.gui.book;
		float RS = book.RS;
		
		book.newModel("EXPL_back")
		.setBackground()
		.setBoundChild(true)
		.setBoundParent(true)
		.setStacked(true)
		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(5)
		.setStackSpacing(2)
		;
		
		book.newModel("EXPL_entry")
		.setRect(0,0,150,30)
		.setBoundParent(true)
		.setStacked(true)
		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setStackSpacing(10)
		;
		
		book.newModelGroup("bloc_selector", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				g.addObject("selection", null);
				
				g.addMetode("get_selection", new nRun() {
					public Object get() { return g.object("selection"); }});

				nWidget ref = g.addWidget("ref", "EXPL_back");
				 
				nWidgetGroup list = gui.addWidgetGroup("scrollist");
				g.addWidgetGroup("list",list);
				list.get("ref").setParent(ref);
				list.metode("set_height", RS * 5f);
				
				ArrayList<nWidgetGroup> entrys = 
						new ArrayList<nWidgetGroup>();
				
				g.addObject("entrys", entrys);
				
				ArrayList<nRun> select_event = 
						new ArrayList<nRun>();
				
				g.addObject("select_event", select_event);
				
				g.addMetode("add_select_event", new nRun() {
					public void run(Object o) {
						select_event.add((nRun)o); }});
				g.addMetode("remove_select_event", new nRun() {
					public void run(Object o) {
						select_event.remove((nRun)o); }});
				g.addMetode("run_select_event", new nRun() { public void run() {
					nRun.runEvents(select_event); 
					nRun.runEvents(select_event, g.object("selection"));
				}});
				
				g.addMetode("explore_bloc", new nRun() {
					public void run(Object o) {
						
						if (o instanceof sValueBloc) {
							
							sValueBloc b = (sValueBloc)o;
							
							nRun run_bloc_del = new nRun() { 
								public void run() {
								// b.parent will be null next frame
								sValueBloc p = b.parent;
								String pref = Applet.copy(p.ref);
								app.addDelayEvent(1, 
									new nRun() { public void run() {
										if (!p.clearing && p.ref.equals(pref)) 
											g.metode("explore_bloc", p); }});
							}};
							
							nRun run_bloc_change = 
									new nRun() { public void run() {
								if (!b.clearing) app.addEventNextFrame(
										new nRun() { public void run() {
									
									if (b.clearing || g.clearing) return;
									
									g.getGroup("list").getGroup("list")
										.metode("clear_entrys");
									
									entrys.clear();
									
									if (b.parent != b) {
										nWidget lt = g.getGroup("list")
												.getGroup("list").get("back");
										if (lt == null) return;
										g.getGroup("list")
										.metodeGet("add_widget_as_entry", 
											g.addWidget(
												"entry_"+g.getGroup("list")
												.getGroup("list").get("back")
												.getChildNb(), 
												gui.addWidget("list_trigger", "..")
												.addEventTrigger(new nRun() {
													public void run() {
														app.addEventNextFrame(run_bloc_del);
													}})
											)
										);
									}
									
									b.runBlocIterator(new nIterator<sValueBloc>() {
										public void run(sValueBloc t) { 
											nWidgetGroup entry = 
													gui.addWidgetGroup(
															"bloc_select_bloc_entry");
											entry.metode("set_bloc", t);
											entry.metode("set_selector", g);
											
											entrys.add(entry);
											
											g.getGroup("list")
											.metodeGet("add_widget_as_entry", 
												g.addWidget(
													"entry_"+g.getGroup("list")
													.getGroup("list").get("back").getChildNb(), 
													entry.get("entry")));
									} } );
								}});
							}};
							
							run_bloc_change.run();
							
							if (g.hasObject("explored_bloc")) {
								sValueBloc old_bloc = g.object("explored_bloc", sValueBloc.class);
								if (old_bloc != b) {
									nRun old_bloc_change = 
											(nRun)g.object("run_bloc_change");
									nRun old_bloc_del = 
											(nRun)g.object("run_bloc_del");
									g.removeObject("explored_bloc", old_bloc);
									g.removeObject("run_bloc_change", old_bloc_change);
									g.removeObject("run_bloc_del", old_bloc_del);
									old_bloc.removeEventChangeThisFrame(old_bloc_change);
									old_bloc.removeEventDelete(old_bloc_del);
								}
							}

							if ( (g.hasObject("explored_bloc") && 
									g.object("explored_bloc", sValueBloc.class) != b) || 
									!g.hasObject("explored_bloc")) {
								g.addObject("explored_bloc", b);
								g.addObject("run_bloc_change", run_bloc_change);
								g.addObject("run_bloc_del", run_bloc_del);
								
								b.addEventChangeThisFrame(run_bloc_change);
								b.addEventDelete(run_bloc_del);
							}
							
							g.setObject("selection", b);
							g.metode("run_select_event");
							
						}
					}
				});
				
				return g;
			} 
		} );
		
		book.newModelGroup("bloc_select_bloc_entry", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget ent = g.addWidget("entry", "list_entry");
				nWidget select = g.addWidget("select", "list_entry_switch_8");
				select.setParent(ent);
				nWidget explo = g.addWidget("explo", "list_entry_trigg_1");
				explo.setParent(ent).setText(">");
				
				g.addMetode("set_bloc", new nRun() { public void run(Object o) {
					g.addObject("bloc", o);
					sValueBloc v = (sValueBloc)o;
					select.setText("  "+v.ref);
					select.addEventSwitchOn(new nRun() { public void run() {
						nWidgetGroup sel = (nWidgetGroup)g.object("selector");
						ArrayList<nWidgetGroup> entrys = 
								(ArrayList<nWidgetGroup>)sel.object("entrys");
						for (nWidgetGroup e : entrys) {
							if (e != g) {
								e.get("select").setOff();
							}
						}
						sel.setObject("selection", v);
						sel.metode("run_select_event");
					}});
					explo.addEventTrigger(new nRun() { public void run() {
						nWidgetGroup sel = (nWidgetGroup)g.object("selector");
						sel.metode("explore_bloc", v);
					}});
				}});
				
				g.addMetode("set_selector", new nRun() { public void run(Object o) {
					g.addObject("selector", o);
				}});
				
				return g;
			} 
		} );
		
		
		
		
		
		book.newModelGroup("value_selector", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				g.addObject("selection", null);
				
				g.addMetode("get_selection", new nRun() {
					public Object get() { return g.object("selection"); }});

				nWidget ref = g.addWidget("ref", "EXPL_back");
				
//				nWidget label = g.addWidget("label", "W_entry");
//				label.setParent(ref);
				
				nWidgetGroup list = gui.addWidgetGroup("scrollist");
				g.addWidgetGroup("list",list);
				list.get("ref").setParent(ref);

				ArrayList<nWidgetGroup> entrys = 
						new ArrayList<nWidgetGroup>();
				
				g.addObject("entrys", entrys);
				
				ArrayList<nRun> select_event = 
						new ArrayList<nRun>();
				
				g.addObject("select_event", select_event);
				
				g.addMetode("add_select_event", new nRun() {
					public void run(Object o) {
						select_event.add((nRun)o); }});
				g.addMetode("remove_select_event", new nRun() {
					public void run(Object o) {
						select_event.remove((nRun)o); }});
				g.addMetode("run_select_event", new nRun() { public void run() {
						nRun.runEvents(select_event); }});
				
				g.addMetode("explore_bloc", new nRun() {
					public void run(Object o) {
						
						if (o instanceof sValueBloc) {
							
							sValueBloc b = (sValueBloc)o;
							
//							label.setText("exploring: /" + b.ref);
							
							nRun run_bloc_del = new nRun() { 
								public void run() {
								// b.parent will be null next frame
								sValueBloc p = b.parent;
								app.addDelayEvent(1, 
									new nRun() { public void run() {
										g.metode("explore_bloc", p); }});
							}};
							
							nRun run_bloc_change = 
									new nRun() { public void run() {
								app.addEventNextFrame(
										new nRun() { public void run() {
									g.getGroup("list").getGroup("list").metode("clear_entrys");
									
									entrys.clear();
									
									b.runValueIterator(new nIterator<sValue>() {
										public void run(sValue t) { 
											nWidgetGroup entry = 
													gui.addWidgetGroup(
															"value_select_val_entry");
											entry.metode("set_val", t);
											entry.metode("set_selector", g);
											
											entrys.add(entry);
											
											g.getGroup("list")
											.metodeGet("add_widget_as_entry", 
												g.addWidget(
													"entry_"+g.getGroup("list")
													.getGroup("list").get("back").getChildNb(), 
													entry.get("entry")));
									} } );
								}});
							}};
							
							run_bloc_change.run();
							
							Object old_bloc_obj = g.object("explored_bloc");
							
							if (old_bloc_obj != null) {
								sValueBloc old_bloc = (sValueBloc)old_bloc_obj;
								if (old_bloc != b) {
									nRun old_bloc_change = 
											(nRun)g.object("run_bloc_change");
									nRun old_bloc_del = 
											(nRun)g.object("run_bloc_del");
									g.removeObject("explored_bloc", old_bloc);
									g.removeObject("run_bloc_change", old_bloc_change);
									g.removeObject("run_bloc_del", old_bloc_del);
									old_bloc.removeEventChangeThisFrame(old_bloc_change);
									old_bloc.removeEventDelete(old_bloc_del);
								}
							}

							if ( (old_bloc_obj != null && 
									(sValueBloc)old_bloc_obj != b) || 
									old_bloc_obj == null) {
								g.addObject("explored_bloc", b);
								g.addObject("run_bloc_change", run_bloc_change);
								g.addObject("run_bloc_del", run_bloc_del);
								
								b.addEventChangeThisFrame(run_bloc_change);
								b.addEventDelete(run_bloc_del);
							}
							
						}
					}
				});
				
				return g;
			} 
		});
		
		book.newModelGroup("value_select_val_entry", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget ent = g.addWidget("entry", "list_entry");
				nWidget select = g.addWidget("select", "list_entry_switch_9");
				select.setParent(ent);
				
				g.addMetode("set_val", new nRun() { public void run(Object o) {
					g.addObject("val", o);
					sValue v = (sValue)o;
					select.setText("  "+v.type + ": " + v.ref);
					select.addEventSwitchOn(new nRun() { public void run() {
						nWidgetGroup sel = (nWidgetGroup)g.object("selector");
						ArrayList<nWidgetGroup> entrys = 
								(ArrayList<nWidgetGroup>)sel.object("entrys");
						for (nWidgetGroup e : entrys) {
							if (e != g) {
								e.get("select").setOff();
							}
						}
						sel.setObject("selection", v);
						sel.metode("run_select_event");
					}});
					select.addEventSwitchOff(new nRun() { public void run() {
						nWidgetGroup sel = (nWidgetGroup)g.object("selector");
						sel.setObject("selection", null);
						sel.metode("run_select_event");
					}});
				}});
				
				g.addMetode("set_selector", new nRun() { public void run(Object o) {
					g.addObject("selector", o);
				}});
				
				return g;
			} 
		} );
		
		
		book.newModelGroup("data_explorer", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", "EXPL_back");
				
				nWidget label = g.addWidget("label", "EXPL_entry");
				label.setParent(ref);
				
				nWidgetGroup list = gui.addWidgetGroup("scrollist");
				g.addWidgetGroup("list",list);
				list.get("ref").setParent(ref);
				
				g.addMetode("explore_bloc", new nRun() {
					public void run(Object o) {
						
						if (o instanceof sValueBloc) {
							
							sValueBloc b = (sValueBloc)o;
							
							label.setText("exploring: /" + b.ref);
							
							nRun run_bloc_del = new nRun() { 
								public void run() {
								// b.parent will be null next frame
								sValueBloc p = b.parent;
								app.addDelayEvent(1, 
									new nRun() { public void run() {
										g.metode("explore_bloc", p); }});
							}};
							
							nRun run_bloc_change = 
									new nRun() { public void run() {
								app.addEventNextFrame(
										new nRun() { public void run() {
									g.getGroup("list").getGroup("list").metode("clear_entrys");
									
									if (b.parent != b) {
										g.getGroup("list")
										.metodeGet("add_widget_as_entry", 
											g.addWidget(
												"entry_"+g.getGroup("list")
												.getGroup("list").get("back").getChildNb(), 
												gui.addWidget("list_trigger", "..")
												.addEventTrigger(new nRun() {
													public void run() {
														app.addEventNextFrame(run_bloc_del);
													}})
											)
										);
									}
									
									b.runBlocIterator(new nIterator<sValueBloc>() {
										public void run(sValueBloc t) { 
											nWidgetGroup entry = 
													gui.addWidgetGroup("data_exp_bloc_entry");
											entry.metode("set_data_expl", g);
											entry.metode("set_bloc", t);
											
											g.getGroup("list")
											.metodeGet("add_widget_as_entry", 
												g.addWidget(
													"entry_"+g.getGroup("list")
													.getGroup("list").get("back").getChildNb(), 
													entry.get("entry")));
									} } );
									
									b.runValueIterator(new nIterator<sValue>() {
										public void run(sValue t) { 
											nWidgetGroup entry = 
													gui.addWidgetGroup("data_exp_val_entry");
											entry.metode("set_val", t);
											g.getGroup("list")
											.metodeGet("add_widget_as_entry", 
												g.addWidget(
													"entry_"+g.getGroup("list")
													.getGroup("list").get("back").getChildNb(), 
													entry.get("entry")));
									} } );
								}});
							}};
							
							run_bloc_change.run();
							
							if (g.hasObject("explored_bloc")) {
								sValueBloc old_bloc = (sValueBloc)g.object("explored_bloc");
								if (old_bloc != b) {
									nRun old_bloc_change = 
											(nRun)g.object("run_bloc_change");
									nRun old_bloc_del = 
											(nRun)g.object("run_bloc_del");
									g.removeObject("explored_bloc", old_bloc);
									g.removeObject("run_bloc_change", old_bloc_change);
									g.removeObject("run_bloc_del", old_bloc_del);
									old_bloc.removeEventChangeThisFrame(old_bloc_change);
									old_bloc.removeEventDelete(old_bloc_del);
								}
							}

							if ( (g.hasObject("explored_bloc") && 
									(sValueBloc)g.object("explored_bloc") != b) || 
									!g.hasObject("explored_bloc")) {
								g.addObject("explored_bloc", b);
								g.addObject("run_bloc_change", run_bloc_change);
								g.addObject("run_bloc_del", run_bloc_del);
								
								b.addEventChangeThisFrame(run_bloc_change);
								b.addEventDelete(run_bloc_del);
							}
							
						}
					}
				});
				
				return g;
			} 
		} );
		
		book.newModelGroup("data_explorer_win", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidgetGroup win = gui.addWidgetGroup("window");
				g.addWidgetGroup("window",win);
				win.metode("add_close");
				win.get("head").setSize(280,30).setText("Data Explorer");
				win.get("close").setPos(250,0);
				win.get("back").setBoundOutspace(5);
				
				nWidgetGroup exp = gui.addWidgetGroup("data_explorer");
				exp.get("ref").setParent(win.get("back"));
				g.addWidgetGroup("exp",exp);
				
				g.addMetode("explore_bloc", new nRun() {
					public void run(Object o) {
						if (o instanceof sValueBloc) {
							sValueBloc b = (sValueBloc)o;
							exp.metode("explore_bloc", b);
						}
					}
				});
				
				return g;
			} 
		} );
		
		book.newModelGroup("data_exp_bloc_entry", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget ent = g.addWidget("entry", "list_entry");
				nWidget trigg = g.addWidget("trigg", "list_entry_trigg_8");
				nWidget open_v = g.addWidget("open_v", "list_entry_trigg_1");
				trigg.setParent(ent);
				open_v.setParent(ent).setText("M");
				
				g.addMetode("set_data_expl", new nRun() { public void run(Object o) {
					g.addObject("data_expl", o); }});
				g.addMetode("set_data_expl", new nRun() { public void run(Object o) {
					g.addObject("data_expl", o); }});
				
				g.addMetode("set_bloc", new nRun() { public void run(Object o) {
					g.addObject("val", o);
					sValueBloc v = (sValueBloc)o;
					trigg.setText("/" + v.ref);
					trigg.addEventTrigger(new nRun() { public void run() {
						app.addEventNextFrame(new nRun() { public void run() {
							((nWidgetGroup)g.object("data_expl"))
								.metode("explore_bloc", v); }}); }});
					open_v.addEventTrigger(new nRun() { public void run() {
						app.addEventNextFrame(new nRun() { public void run() {
//							v.open_viewer();
						}});
					}});
				}});
				
				return g;
			} 
		} );
		
		book.newModelGroup("data_exp_val_entry", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget ent = g.addWidget("entry", "list_entry");
				nWidget label = g.addWidget("label", "list_entry_label_8");
				nWidget open_v = g.addWidget("open_v", "list_entry_trigg_1");
				label.setParent(ent);
				open_v.setParent(ent).setText("M");
				
				g.addMetode("set_val", new nRun() { public void run(Object o) {
					g.addObject("val", o);
					sValue v = (sValue)o;
					label.setText("  "+v.type + ": " + v.ref + " = " + v.getString());
					open_v.addEventTrigger(new nRun() { public void run() {
//						app.addEventNextFrame(new nRunnable() { public void run() {
							v.open_viewer();
//						}});
					}});
				}});
				
				return g;
			} 
		} );
		
		book.newModelGroup("bloc_viewer", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidgetGroup win = gui.addWidgetGroup("window");
				g.addWidgetGroup("window",win);
				win.metode("add_close");
				win.get("head").setSize(160,30).setText("");
				win.get("close").setPos(130,0);
				win.get("back")
				.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
				.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
				;
				
				g.addMetode("view_bloc", new nRun() {
					public void run(Object o) {
						if (o instanceof sValueBloc) {
							sValueBloc b = (sValueBloc)o;
							win.get("head").setText("viewing: "+b.ref);
							g.addWidget("adress", gui.addWidget("W_entry")
									.setParent(win.get("back"))
									.setText("adress: "+b.adress)
									);
						}
					}
				});
				
				return g;
			} 
		} );

		book.newModelGroup("value_viewer", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidgetGroup win = gui.addWidgetGroup("window");
				g.addWidgetGroup("window",win);
				win.metode("add_close");
				win.get("head").setSize(160,30).setText("");
				win.get("close").setPos(130,0);
				win.get("back")
				.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
				.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
				;
				
				g.addMetode("view_val", new nRun() {
					public void run(Object o) {
						if (o instanceof sValue) {
							sValue b = (sValue)o;
							win.get("head").setText("viewing: "+b.ref);
							g.addWidget("type", gui.addWidget("W_entry")
								.setParent(win.get("back"))
								.setText("type: "+b.type)
								);
							g.addWidget("adress", gui.addWidget("W_entry")
									.setParent(win.get("back"))
									.setText("adress: "+b.adress)
									);
							g.addWidget("val", gui.addWidget("W_entry")
									.setParent(win.get("back"))
									.setText("NOSYNC value: "+b.getString())
									);
						}
					}
				});
				
				return g;
			} 
		} );
	}
}
