// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.codingchallenge;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

final class MyJSONParser implements JSONParser {

  @Override
  public JSON parse(String in) throws IOException {

  	JSON retJson = new MyJSON();
  	//System.out.println("in: " + in);

  	//determine whether object has multiple items
  	int comma_pos = in.indexOf(",");
  	int quote_ctr = 0;
  	int open_ctr = 0;
  	int close_ctr = 0;
  	if (comma_pos != -1) {
  		for (int i=0; i<in.substring(0,comma_pos).length(); i++) {
  			if (in.charAt(i)=='\"') { //if char is quote
  				if (i>0 && in.charAt(i-1)!='\\') { //and isn't escaped before
  					quote_ctr++; //add to quote counter
  				}
  			} else if (in.charAt(i)=='{') { 
  				open_ctr++; 
  			} else if (in.charAt(i)=='}') {
  				close_ctr++;
  			}
  		}

	  	//if even # quotes, comma is being used to separate 
	  	//if even open/close <=1, can be sure code isn't separating value
	  	if (quote_ctr%2==0 && open_ctr-close_ctr<=1) {
	  		String[] objs = in.split(",");
	  		for (String obj: objs) { //for each object

	  			//add necessary missing brackets for parsing
	  			if (obj.indexOf("{")==-1) { obj = "{" + obj; } 
	  			if (obj.indexOf("}")==-1) { obj += "}"; }

	  			JSON obj_json = parse(obj); //parse results for this obj

	  			//Find out whether to setObject or setString
	  			Collection<String> names = new HashSet<>();
	  			Iterator<String> names_iter;
	  			String this_name;
	  			obj_json.getObjects(names);
	  			if (names.size()>0) { //if this obj's val is an obj
	  				names_iter = names.iterator();
	  				this_name = names_iter.next();
	  				retJson = retJson.setObject(this_name, obj_json.getObject(this_name));
	  			} else { //if this obj's val is a string
	  				obj_json.getStrings(names);
	  				names_iter = names.iterator();
	  				this_name = names_iter.next();
	  				retJson = retJson.setString(this_name, obj_json.getString(this_name));
	  			}
	  		}
	  		return retJson;
	  	}
	}
  	//just looking at one object
    String[] components = in.split(":",2); //split input into name and value
    components[0] = components[0].replace("{","").replace("\"", "").trim(); //clear brackets,quotes

    if (components.length < 2) { //empty JSON
    	//creates empty object when given empty strings
    	retJson = retJson.setString("","");

    //if no opening bracket, not a JSON-Lite object (i.e is String)
    } else if (components[1].indexOf('{') == -1) {
    	//clear String value of close bracket and quotes
    	components[1] = components[1].replace("}","").replace("\"","").trim();
    	retJson = retJson.setString(components[0], components[1]);

    } else { //value is an Object
    	//clear Object value of extra close bracket at end
    	components[1] = components[1].substring(0, components[1].lastIndexOf('}'));
    	retJson = retJson.setObject(components[0], parse(components[1]));
    }
    return retJson;
  }
}