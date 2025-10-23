package org.frogcy.furniturecommon.entity.order;

public enum OrderStatus {
    NEW{
        @Override
        public String defaultDescription() {
            return "Order was placed by the customer";
        }
    },
    CANCELLED{
        @Override
        public String defaultDescription() {
            return "Order was rejected";
        }
    },
    PROCESSING{
        @Override
        public String defaultDescription() {

            return "Order is being processing";
        }
    },
    PACKAGED{
        @Override
        public String defaultDescription() {

            return "Products were packaged";
        }
    },
    PICKED{
        @Override
        public String defaultDescription() {

            return "Shipper picked the package";
        }
    },
    SHIPPING{
        @Override
        public String defaultDescription() {
            return "Shipper is delivering the package";
        }
    },
    DELIVERED{
        @Override
        public String defaultDescription() {
            return "Customer received products";
        }
    },
    RETURN_REQUESTED{
        @Override
        public String defaultDescription(){
            return "Customer sent request to return purchase";
        }
    },
    RETURNED{
        @Override
        public String defaultDescription() {
            return "Products were returned";
        }
    },
    RETURN_REJECTED{
        @Override
        public String defaultDescription() {
            return "Admin rejected return products";
        }
    };

    public abstract String defaultDescription();

}
