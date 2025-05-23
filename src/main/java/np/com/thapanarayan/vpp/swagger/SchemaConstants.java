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
                "data": {
                    "batteries": [
                        {
                            "id": 11,
                            "name": "Akunda Bay",
                            "postcode": "2084",
                            "capacity": 13500
                        },
                        {
                            "id": 16,
                            "name": "Norfolk Island",
                            "postcode": "2899",
                            "capacity": 13500
                        },
                        {
                            "id": 17,
                            "name": "Ootha",
                            "postcode": "2875",
                            "capacity": 13500
                        },
                        {
                            "id": 15,
                            "name": "University of Melbourne",
                            "postcode": "3010",
                            "capacity": 85000
                        },
                        {
                            "id": 12,
                            "name": "Werrington County",
                            "postcode": "2747",
                            "capacity": 13500
                        }
                    ],
                    "statistics": {
                        "totalWattCapacity": 139000.0,
                        "averageWattCapacity": 27800.0,
                        "numBatteries": 5
                    }
                }
            }
            """;
}
