package org.ihtsdo.otf.ts.rf2.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.ihtsdo.otf.ts.rf2.SimpleRefSetMember;

/**
 * Concrete implementation of {@link SimpleRefSetMember}.
 */
@Entity
@Table(name = "simple_refset_members")
@Audited
public class SimpleRefSetMemberJpa extends AbstractConceptRefSetMember
    implements SimpleRefSetMember {

  /**
   * Instantiates an empty {@link SimpleRefSetMemberJpa}.
   */
  public SimpleRefSetMemberJpa() {
    // do nothing
  }
  
  /**
   * Instantiates a {@link SimpleRefSetMemberJpa} from the specified parameters.
   *
   * @param member the member
   */
  public SimpleRefSetMemberJpa(SimpleRefSetMember member) {
    super(member);
  }
  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString()
        + (this.getConcept() == null ? null : this.getConcept()
            .getTerminologyId());
  }

}
