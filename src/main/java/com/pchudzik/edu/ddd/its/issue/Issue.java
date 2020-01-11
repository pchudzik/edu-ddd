package com.pchudzik.edu.ddd.its.issue;

class Issue {
    IssueId id;

    IssueDescription issueDescription;

    Issue(IssueId id, String title, String description) {
        this.id = id;
        this.issueDescription = new IssueDescription(title, description);
    }

    private static class IssueDescription {
        private String title;
        private String description;

        public IssueDescription(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }
}
