package io.theappx.simpletodo.helper;

public enum RepeatInterval {
    ONE_TIME {
        @Override
        public String toString() {
            return "Does not repeat";
        }
    }, DAILY {
        @Override
        public String toString() {
            return "Daily";
        }
    }, WEEKLY {
        @Override
        public String toString() {
            return "Weekly";
        }
    }, MONTHLY {
        @Override
        public String toString() {
            return "Monthly";
        }
    }, YEARLY {
        @Override
        public String toString() {
            return "Yearly";
        }
    }
}
