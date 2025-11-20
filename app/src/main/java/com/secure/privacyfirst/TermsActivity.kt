package com.secure.privacyfirst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.secure.privacyfirst.ui.theme.PrivacyFirstTheme

class TermsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrivacyFirstTheme {
                TermsScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms and Conditions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Last updated: [Date]",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nPlease read these terms and conditions carefully before using Our Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nInterpretation and Definitions",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nInterpretation",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "\nThe words of which the initial letter is capitalized have meanings defined under the following conditions. The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nDefinitions",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "\nFor the purposes of these Terms and Conditions:",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * Affiliate means an entity that controls, is controlled by or is under common control with a party, where control means ownership of 50% or more of the shares, equity interest or other securities entitled to vote for election of directors or other managing authority.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * Country refers to: India",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * Company (referred to as either the Company, We, Us or Our in this Agreement) refers to PrivacyFirst.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * Device means any device that can access the Service such as a computer, a cellphone or a digital tablet.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * Service refers to the Website.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * Terms and Conditions (also referred as Terms) mean these Terms and Conditions that form the entire agreement between You and the Company regarding the use of the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * Third-party Social Media Service means any services or content (including data, information, products or services) provided by a third-party that may be displayed, included or made available by the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * You means the individual accessing or using the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nAcknowledgment",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nThese are the Terms and Conditions governing the use of this Service and the agreement that operates between You and the Company. These Terms and Conditions set out the rights and obligations of all users regarding the use of the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nYour access to and use of the Service is conditioned on Your acceptance of and compliance with these Terms and Conditions. These Terms and Conditions apply to all visitors, users and others who access or use the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nBy accessing or using the Service You agree to be bound by these Terms and Conditions. If You disagree with any part of these Terms and Conditions then You may not access the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nYou represent that you are over the age of 18. The Company does not permit those under 18 to use the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nYour access to and use of the Service is also conditioned on Your acceptance of and compliance with the Privacy Policy of the Company. Our Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your personal information when You use the Application or the Website and tells You about Your privacy rights and how the law protects You. Please read Our Privacy Policy carefully before using Our Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nLinks to Other Websites",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nOur Service may contain links to third-party web sites or services that are not owned or controlled by the Company.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nThe Company has no control over, and assumes no responsibility for, the content, privacy policies, or practices of any third-party web sites or services. You further acknowledge and agree that the Company shall not be responsible or liable, directly or indirectly, for any damage or loss caused or alleged to be caused by or in connection with the use of or reliance on any such content, goods or services available on or through any such web sites or services.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nWe strongly advise You to read the terms and conditions and privacy policies of any third-party web sites or services that You visit.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nTermination",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nWe may terminate or suspend Your access immediately, without prior notice or liability, for any reason whatsoever, including without limitation if You breach these Terms and Conditions.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nUpon termination, Your right to use the Service will cease immediately.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nLimitation of Liability",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nNotwithstanding any damages that You might incur, the entire liability of the Company and any of its suppliers under any provision of this Terms and Your exclusive remedy for all of the foregoing shall be limited to the amount actually paid by You through the Service or 100 USD if You haven't purchased anything through the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nTo the maximum extent permitted by applicable law, in no event shall the Company or its suppliers be liable for any special, incidental, indirect, or consequential damages whatsoever (including, but not limited to, damages for loss of profits, for loss of data or other information, for business interruption, for personal injury, for loss of privacy arising out of or in any way related to the use of or inability to use the Service, third-party software and/or third-party hardware used with the Service, or otherwise in connection with any provision of this Terms), even if the Company or any supplier has been advised of the possibility of such damages and even if the remedy fails of its essential purpose.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nSome states do not allow the exclusion of implied warranties or limitation of liability for incidental or consequential damages, which means that some of the above limitations may not apply. In these states, each party's liability will be limited to the greatest extent permitted by law.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nGoverning Law",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nThe laws of the Country, excluding its conflicts of law rules, shall govern this Terms and Your use of the Service. Your use of the Application may also be subject to other local, state, national, or international laws.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nDisputes Resolution",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nIf You have any concern or dispute about the Service, You agree to first try to resolve the dispute informally by contacting the Company.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nFor European Union (EU) Users",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nIf You are a European Union consumer, you will benefit from any mandatory provisions of the law of the country in which you are resident in.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nUnited States Legal Compliance",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nYou represent and warrant that (i) You are not located in a country that is subject to the United States government embargo, or that has been designated by the United States government as a terrorist supporting country, and (ii) You are not listed on any United States government list of prohibited or restricted parties.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nSeverability and Waiver",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nSeverability",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "\nIf any provision of these Terms is held to be unenforceable or invalid, such provision will be changed and interpreted to accomplish the objectives of such provision to the greatest extent possible under applicable law and the remaining provisions will continue in full force and effect.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nWaiver",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "\nExcept as provided herein, the failure to exercise a right or to require performance of an obligation under this Terms shall not effect a party's ability to exercise such right or require such performance at any time thereafter nor shall be the waiver of a breach constitute a waiver of any subsequent breach.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nTranslation Interpretation",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nThese Terms and Conditions may have been translated if We have made them available to You on our Service. You agree that the original English text shall prevail in the case of a dispute.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nChanges to These Terms and Conditions",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nWe reserve the right, at Our sole discretion, to modify or replace these Terms at any time. If a revision is material We will make reasonable efforts to provide at least 30 days notice prior to any new terms taking effect. What constitutes a material change will be determined at Our sole discretion.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nBy continuing to access or use Our Service after those revisions become effective, You agree to be bound by the revised terms. If You do not agree to the new terms, in whole or in part, please stop using the website and the Service.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\nContact Us",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "\nIf you have any questions about these Terms and Conditions, You can contact us:",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\n  * By visiting this page on our website: [Your contact page URL]",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
