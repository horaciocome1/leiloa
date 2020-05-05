package io.github.horaciocome1.leiloa.data.config

object RemoteConfigDefaults {

    // INPUT FILTER LENGTH
    const val MAX_LENGTH_COMPANY_DOMAIN = "max_length_company_domain"
    const val MAX_LENGTH_PRODUCT_ID = "max_length_product_id"
    const val MAX_LENGTH_TERMS_AND_CONDITIONS = "max_length_terms_and_conditions"
    const val MAX_LENGTH_START_PRICE = "max_length_start_price"

    const val START_ACTIVE_BY_DEFAULT = "start_active_by_default"

    val DEFAULTS: Map<String, Any> by lazy {
        mapOf(
            MAX_LENGTH_COMPANY_DOMAIN to 70,
            MAX_LENGTH_PRODUCT_ID to 70,
            MAX_LENGTH_TERMS_AND_CONDITIONS to 300,
            MAX_LENGTH_START_PRICE to 5,
            START_ACTIVE_BY_DEFAULT to true
        )
    }

}