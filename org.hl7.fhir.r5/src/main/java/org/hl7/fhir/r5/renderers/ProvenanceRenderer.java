package org.hl7.fhir.r5.renderers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Provenance;
import org.hl7.fhir.r5.model.Provenance.ProvenanceAgentComponent;
import org.hl7.fhir.r5.model.Reference;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.model.UriType;
import org.hl7.fhir.r5.renderers.utils.BaseWrappers.ResourceWrapper;
import org.hl7.fhir.r5.renderers.utils.RenderingContext;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

public class ProvenanceRenderer extends ResourceRenderer {
  
  public ProvenanceRenderer(RenderingContext context) {
    super(context);
  }

  public boolean render(XhtmlNode x, Resource prv) throws UnsupportedEncodingException, IOException {
    return render(x, (Provenance) prv);
  }
  
  public boolean render(XhtmlNode x, Provenance prv) throws UnsupportedEncodingException, IOException {
    boolean hasExtensions = false;

    if (!prv.getTarget().isEmpty()) {
      if (prv.getTarget().size() == 1) {
        XhtmlNode p = x.para();
        p.tx(context.formatPhrase(RenderingContext.PROV_PROV)+" ");
        renderReference(prv, p, prv.getTargetFirstRep());
      } else {
        x.para().tx(context.formatPhrase(RenderingContext.PROV_PROVE)+" ");
        XhtmlNode ul = x.ul();
        for (Reference ref : prv.getTarget()) {
          renderReference(prv, ul.li(), ref);
        }
      }
    }
    // summary table
    x.para().tx(context.formatPhrase(RenderingContext.GENERAL_SUMM));
    XhtmlNode t = x.table("grid");
    XhtmlNode tr;
    if (prv.hasOccurred()) {
      tr = t.tr();
      tr.td().tx(context.formatPhrase(RenderingContext.PROV_OCC));
      if (prv.hasOccurredPeriod()) {
        renderPeriod(tr.td(), prv.getOccurredPeriod());
      } else {
        renderDateTime(tr.td(), prv.getOccurredDateTimeType());        
      }
    }
    if (prv.hasRecorded()) {
      tr = t.tr();
      tr.td().tx(context.formatPhrase(RenderingContext.PROV_REC));
      tr.td().addText(displayDateTime(prv.getRecordedElement()));
    }
    if (prv.hasPolicy()) {
      tr = t.tr();
      tr.td().tx(context.formatPhrase(RenderingContext.PROV_POL));
      if (prv.getPolicy().size() == 1) {
        renderUri(tr.td(), prv.getPolicy().get(0));
      } else {
        XhtmlNode ul = tr.td().ul();
        for (UriType u : prv.getPolicy()) {
          renderUri(ul.li(), u);
        }
      }
    }
    if (prv.hasLocation()) {
      tr = t.tr();
      tr.td().tx(context.formatPhrase(RenderingContext.GENERAL_LOCATION));
      renderReference(prv, tr.td(), prv.getLocation());      
    }
    if (prv.hasActivity()) {
      tr = t.tr();
      tr.td().tx(context.formatPhrase(RenderingContext.PROV_ACT));
      renderCodeableConcept(tr.td(), prv.getActivity(), false);
    }

    boolean hasType = false;
    boolean hasRole = false;
    boolean hasOnBehalfOf = false;
    for (ProvenanceAgentComponent a : prv.getAgent()) {
      hasType = hasType || a.hasType(); 
      hasRole = hasRole || a.hasRole(); 
      hasOnBehalfOf = hasOnBehalfOf || a.hasOnBehalfOf(); 
    }    
    x.para().b().tx(context.formatPhrase(RenderingContext.PROV_AGE));
    t = x.table("grid");
    tr = t.tr();
    if (hasType) {
      tr.td().b().tx(context.formatPhrase(RenderingContext.GENERAL_TYPE));
    }
    if (hasRole) {
      tr.td().b().tx(context.formatPhrase(RenderingContext.PROV_ROLE));
    }
    tr.td().b().tx(context.formatPhrase(RenderingContext.PROV_WHO));
    if (hasOnBehalfOf) {
      tr.td().b().tx(context.formatPhrase(RenderingContext.PROV_BEHALF));
    }
    for (ProvenanceAgentComponent a : prv.getAgent()) {
      tr = t.tr();
      if (hasType) {
        if (a.hasType()) {
          renderCodeableConcept(tr.td(), a.getType(), false);         
        } else {
          tr.td();
        }
      }        
      if (hasRole) {
        if (a.hasRole()) {
          if (a.getRole().size() == 1) {
            renderCodeableConcept(tr.td(), a.getType(), false);
          } else {
            XhtmlNode ul = tr.td().ul();
            for (CodeableConcept cc : a.getRole()) {
              renderCodeableConcept(ul.li(), cc, false);
            }
          }
        } else {
          tr.td();
        }
      }
      if (a.hasWho()) {
        renderReference(prv, tr.td(), a.getWho());         
      } else {
        tr.td();
      }
      if (hasOnBehalfOf) {
        if (a.hasOnBehalfOf()) {
          renderReference(prv, tr.td(), a.getOnBehalfOf());         
        } else {
          tr.td();
        }
      }
    }
    // agent table

    return hasExtensions; 
  }

  public String display(Resource dr) throws UnsupportedEncodingException, IOException {
    return display((Provenance) dr);
  }

  public String display(Provenance prv) throws UnsupportedEncodingException, IOException {
    return (context.formatPhrase(RenderingContext.PROV_FOR, displayReference(prv, prv.getTargetFirstRep()))+" ");
  }
 
  @Override
  public String display(ResourceWrapper r) throws UnsupportedEncodingException, IOException {
    return (context.formatPhrase(RenderingContext.GENERAL_TODO));
  }

}