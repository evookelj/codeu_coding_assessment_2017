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

/*Outline of parsing algorithm
0. If invalid parametere, throw exception. Otherwise continue
1. If comma in param and comma separates object pieces, parse individual objs and add
2. (Definitely dealing with a single object) Separate name and value
3. Determine whether val is STRING (just add) or OBJ (parse obj, add parsed obj)
*/

final class MyJSONParser implements JSONParser {

  @Override
  public JSON parse(String in) throws IOException {

    /*====STEP 0: Check for invalid str before running algo====
        -invalid escape characters
        -unescaped quotes
    */
    for (int i=0; i<in.length()-1; i++) {
      if (in.charAt(i)=='\\' && (!(in.charAt(i+1)=='t' || in.charAt(i+1)=='n' || in.charAt(i+1)=='\"'))) { //invalud escape chr
        throw new IOException();
      }
    }

  	//====STEP 1: determine whether object has multiple items====
    JSON retJson = new MyJSON();
  	int comma_pos = in.indexOf(",");
  	int quote_ctr = 0;
  	int open_ctr = 0;
  	int close_ctr = 0;
  	if (comma_pos != -1) {
  		for (int i=0; i<in.substring(0,comma_pos).length(); i++) {
  			if (in.charAt(i)=='\"') {
  					quote_ctr++;
  			} else if (in.charAt(i)=='{') { 
  				  open_ctr++; 
  			} else if (in.charAt(i)=='}') {
  				  close_ctr++;
  			} else if (i>0 && in.charAt(i)=='\"' && in.charAt(i+1)=='\\') {
            quote_ctr++; //don't count esacped quote
        }
  		}

	  	//if even # quotes, comma is being used to separate 
	  	//if even open/close <=1, can be sure code isn't separating value
	  	if (quote_ctr%2==0 && open_ctr-close_ctr<=1) {
	  		String[] objs = in.split(",",2);
	  		for (String obj: objs) {

	  			//add necessary missing brackets for parsing
	  			if (in.indexOf("\\{")==-1 || close_ctr>open_ctr) { obj = "{" + obj; } 
	  			else if (in.indexOf("\\}")==-1 || open_ctr>close_ctr) { obj += "}"; }

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

    //====STEP 2: Separate name and value====
    String[] components = in.split(":",2); //split input into name and value
    components[0] = components[0].replace("{","").replaceFirst("\"", "").trim(); //clean str
    int end_quote_pos = components[0].lastIndexOf('\"');
    if (end_quote_pos>=0) {
      components[0] = components[0].substring(0,end_quote_pos)+components[0].substring(end_quote_pos+1);
    }

    //check components[0] for invalid unescaped quote
    for (int i=0; i<components[0].length()-1; i++) {
      if (components[0].charAt(i+1)=='\"' && components[0].charAt(i)!='\\') {
        throw new IOException();
      }
    }

    if (components.length < 2) { //empty JSON
    	retJson = retJson.setString("",""); //creates empty obj when given empty strs

    //====STEP 3: Add STRING or add OBJECT recursively====
    } else if (components[1].indexOf('{') == -1) { //no opening bracket->not JSON-Lite object

    	components[1] = components[1].replace("}","").replaceFirst("\"","").trim(); //clean str
      end_quote_pos = components[1].lastIndexOf('\"');
      if (end_quote_pos>=0) {
        components[1] = components[1].substring(0,end_quote_pos)+components[1].substring(end_quote_pos+1);
      };
      
      //check components[1] for invalid unescaped quote
      for (int i=0; i<components[1].length()-1; i++) {
        if (components[1].charAt(i+1)=='\"' && components[1].charAt(i)!='\\') {
          throw new IOException();
        }
      }

    	retJson = retJson.setString(components[0], components[1]);

    } else { //value is an Object
    	components[1] = components[1].substring(0, components[1].lastIndexOf('}')); //clean str
    	retJson = retJson.setObject(components[0], parse(components[1])); //recursively parse value obj
    }

    return retJson;
  }
}