"addressTypeChange" : {
"collectionCase": {
"id": "${caseId}",
<#if estabType == "CE">
    "ceExpectedCapacity":"${usualResidents}",
</#if>
"address" : {
    "addressType":"${estabType}"
<#if estabType == "CE">
    "estabType":"${spgOutcome.ceDetails.establishmentType}",
    "organisationName": "${spgOutcome.ceDetails.establishmentName}"
</#if>
}
}
}