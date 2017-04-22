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

final class MyJSONParser implements JSONParser {

  @Override
  public JSON parse(String in) throws IOException {

  	JSON retJson = new MyJSON();
  	System.out.println("\nSTRING IN: " + in);

  	//split input into name and value
    String[] components = in.split(":",2);
    //clear name of brackets and quotes
    components[0] = components[0].replace("{","").replace("\"", "").trim();
    System.out.println("COMPONENT[0]: " + components[0]);

    if (components.length < 2) { //empty JSON
    	//creates empty object when given empty strings
    	retJson = retJson.setString("","");

    //if no opening bracket, not a JSON-Lite object (i.e is String)
    } else if (components[1].indexOf('{') == -1) {

    	//clear String value of close bracket and quotes
    	components[1] = components[1].replace("}","").replace("\"","").trim();
    	retJson = retJson.setString(components[0], components[1]);
    	System.out.println("Component[1] IS STRING: " + retJson.getString(components[0]));
    } else {
    	//clear Object value of extra close bracket at end
    	components[1] = components[1].substring(0, components[1].lastIndexOf('}'));
    	System.out.println("Component[1] IS OBJECT: " + components[1]);
    	retJson = retJson.setObject(components[0], parse(components[1]));
    }
    System.out.println("");
    return retJson;
  }
}
