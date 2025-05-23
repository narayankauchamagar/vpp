package np.com.thapanarayan.vpp.swagger;

public class SchemaConstants {

    public static final String BATTERY_RESPONSE_DTO = """
            {
                "message": "Batteries saved successfully",
                "data": [
                    {
                        "id": 1,
                        "name": "Cannington",
                        "postcode": "6107",
                        "capacity": 13500
                    },
                    {
                        "id": 2,
                        "name": "Midland",
                        "postcode": "6057",
                        "capacity": 50500
                    }
                ],
                "error": {
                        "message": "Duplicate Batteries Found",
                        "metadata": [
                            {
                             "name": "Koolan Island",
                             "postcode": "6733",
                             "capacity": 10000
                            },
                            {
                             "name": "Armadale",
                             "postcode": "6992",
                             "capacity": 25000
                             }
                        ]
                }
            """;
    public static final String BATTERY_SEARCH_RESPONSE_BAD_REQUEST = """
            {
                "message": "Invalid Request",
                "error": {
                    "message": "Invalid postcode range: min_postcode cannot be greater than max_postcode."
                }
            }
            """;
    public static final String BATTERY_SEARCH_LIST = """
            {
                "message": "Batteries fetched successfully",
                "data": [
                    {
                        "id": 1,
                        "name": "Cannington",
                        "postcode": "6107",
                        "capacity": 13500
                    },
                    {
                        "id": 2,
                        "name": "Midland",
                        "postcode": "6057",
                        "capacity": 50500
                    }
                ]
            }
            """;
}
