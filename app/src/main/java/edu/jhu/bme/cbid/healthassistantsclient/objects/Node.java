package edu.jhu.bme.cbid.healthassistantsclient.objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amal Afroz Alam on 21, April, 2016.
 * Contact me: contact@amal.io
 */
public class Node implements Serializable{

    private String id;
    private String text;
    private String language;
    private String inputType;
    private String physicalExams;
    private List<Node> optionsList;
    private String associatedComplaint;
    //private List associatedComplaints; //To be implemented only when
    private String jobAidFile;
    private String jobAidType;

    private boolean complaint;
    private boolean required;
    private boolean terminal;
    private boolean hasAssociations;
    private boolean aidAvailable;
    private boolean selected;
    private boolean subSelected;

    public Node(JSONObject jsonNode) {
        try {
            this.id = jsonNode.getString("id");

            this.text = jsonNode.getString("text");

            JSONArray optionsArray = jsonNode.optJSONArray("options");
            if (optionsArray == null) {
                this.terminal = true;
            } else {
                this.terminal = false;
                this.optionsList = createOptions(optionsArray);
            }

            this.language = jsonNode.optString("language");

            this.inputType = jsonNode.optString("input-type");

            this.physicalExams = jsonNode.optString("perform-physical-exam");
            if (!physicalExams.isEmpty()) {
                this.complaint = true;
            } else {
                this.complaint = false;
            }

            this.jobAidFile = jsonNode.optString("job-aid-file");
            if (!jobAidFile.isEmpty()) {
                this.jobAidType = jsonNode.optString("job-aid-type");
                this.aidAvailable = true;
            } else {
                this.aidAvailable = false;
            }

            this.associatedComplaint = jsonNode.optString("associated-complaint");
            if (associatedComplaint.isEmpty()) {
                this.hasAssociations = false;
            } else {
                this.hasAssociations = true;
            }

            this.selected = false;

            this.required = false;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Node(Node another) {
        this.id = another.id;
        this.text = another.text;
        this.optionsList = another.optionsList;
        this.terminal = another.terminal;
        this.language = another.language;
        this.inputType = another.inputType;
        this.physicalExams = another.physicalExams;
        this.complaint = another.complaint;
        this.jobAidFile = another.jobAidFile;
        this.jobAidType = another.jobAidType;
        this.aidAvailable = another.aidAvailable;
        this.associatedComplaint = another.associatedComplaint;
        this.hasAssociations = another.hasAssociations;
        this.selected = false;
        this.required = another.required;
    }

    private List<Node> createOptions(JSONArray jsonArray) {
        List<Node> createdOptions = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject current = jsonArray.getJSONObject(i);
                createdOptions.add(i, new Node(current));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return createdOptions;
    }

    public String type() {
        return inputType;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public boolean isComplaint() {
        return complaint;
    }

    public boolean isRequired(){
        return required;
    }

    public String language() {
        return language;
    }

    public void addLanguage(String newText) {
        if (language.contains("_")) {
            language = language.replace("_", newText);
        } else {
            language = language + newText;
        }
    }

    public String text() {
        return text;
    }

    public String id() {
        return id;
    }

    public int size() {
        return optionsList.size();
    }

    public boolean hasAssociations() {
        return hasAssociations;
    }

    public String getAssociatedComplaint() {
        return associatedComplaint;
    }

    public boolean isAidAvailable() {
        return aidAvailable;
    }

    public String getExams() {
        return physicalExams;
    }

    public List<Node> getOptionsList() {
        return optionsList;
    }

    public Node getOptionByName(String name) {
        Node foundNode = null;
        for (Node node : optionsList) {
            if (node.text().equals(name)) {
                foundNode = node;
            }
        }
        return foundNode;
    }

    public Node getOption(int i) {
        return optionsList.get(i);
    }

    public void addOptions(Node node) {
        optionsList.add(node);
    }

    public String getJobAidFile() {
        return jobAidFile;
    }

    public String getJobAidType() {
        return jobAidType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setUnselected() {
        selected = false;
    }

    public void toggleSelected() {
        selected = !selected;
    }

    public void setSelected() {
        selected = true;
    }

    public boolean anySubSelected() {
        if(!terminal){
            for (int i = 0; i < optionsList.size(); i++) {
                if (optionsList.get(i).isSelected()) {
                    subSelected = true;
                    break;
                } else {
                    subSelected = false;
                }
            }
            return subSelected;
        } else {
            return false;
        }
    }

    public void changeText(String newText) {
        this.text = newText;
    }

    public String formLanguage() {
        List<String> stringsList = new ArrayList<>();
        List<Node> mOptions = optionsList;
        for (int i = 0; i < mOptions.size(); i++) {
            if (mOptions.get(i).isSelected()) {
                stringsList.add(mOptions.get(i).language());
                if (!mOptions.get(i).isTerminal()) {
                    stringsList.add(mOptions.get(i).formLanguage());
                }
            }
        }

        String languageSeparator = ", ";
        String mLanguage = "";
        for (int i = 0; i < stringsList.size(); i++) {
            if (i == 0) {
                if (!stringsList.get(i).isEmpty()) {
                    mLanguage = mLanguage.concat(stringsList.get(i));
                }
            } else {
                if (!stringsList.get(i).isEmpty()) {
                    mLanguage = mLanguage.concat(languageSeparator + stringsList.get(i));
                }
            }
        }
        Log.d("Form language", mLanguage);
        return mLanguage;
    }

    public String generateLanguage() {
        String raw = this.formLanguage();
        String formatted;
        if (Character.toString(raw.charAt(0)).equals(",")) {
            formatted = raw.substring(2);
        } else {
            formatted = raw;
        }
        return formatted;
    }

    public ArrayList<String> getSelectedAssociations() {
        ArrayList<String> selectedAssociations = new ArrayList<>();
        List<Node> mOptions = optionsList;
        for (int i = 0; i < mOptions.size(); i++) {
            if (mOptions.get(i).isSelected() & mOptions.get(i).hasAssociations()) {
                selectedAssociations.add(mOptions.get(i).getAssociatedComplaint());
                if (!mOptions.get(i).isTerminal()) {
                    selectedAssociations.addAll(mOptions.get(i).getSelectedAssociations());
                }
            }
        }
        return selectedAssociations;
    }

    public void removeOptionsList(){
        this.optionsList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getPhysicalExams() {
        return physicalExams;
    }

    public void setPhysicalExams(String physicalExams) {
        this.physicalExams = physicalExams;
    }

    public void setOptionsList(List<Node> optionsList) {
        this.optionsList = optionsList;
    }

    public void setAssociatedComplaint(String associatedComplaint) {
        this.associatedComplaint = associatedComplaint;
    }

    public void setJobAidFile(String jobAidFile) {
        this.jobAidFile = jobAidFile;
    }

    public void setJobAidType(String jobAidType) {
        this.jobAidType = jobAidType;
    }

    public void setComplaint(boolean complaint) {
        this.complaint = complaint;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public boolean isHasAssociations() {
        return hasAssociations;
    }

    public void setHasAssociations(boolean hasAssociations) {
        this.hasAssociations = hasAssociations;
    }

    public void setAidAvailable(boolean aidAvailable) {
        this.aidAvailable = aidAvailable;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSubSelected() {
        return subSelected;
    }

    public void setSubSelected(boolean subSelected) {
        this.subSelected = subSelected;
    }
}

